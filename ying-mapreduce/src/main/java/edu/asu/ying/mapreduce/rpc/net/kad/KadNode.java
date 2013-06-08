package edu.asu.ying.mapreduce.rpc.net.kad;
/*
 * KadNode.java
 * DEPRECATED
 * FOR REFERENCE ONLY
 */

/*
 * KadNode.java
 * Manages a local KAD network endpoint and traffic to and from the network.
 
package edu.asu.ying.mapreduce.kad;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
// Google dependency injection
import com.google.inject.Guice;
import com.google.inject.Injector;
// OpenKAD
import il.technion.ewolf.kbr.*;
import il.technion.ewolf.kbr.openkad.KadNetModule;

import edu.asu.ying.mapreduce.logging.Logger;
import edu.asu.ying.mapreduce.config.Configuration;
import edu.asu.ying.mapreduce.filesystem.DistributedChunk;
import edu.asu.ying.mapreduce.filesystem.DistributedFilesystem;


public final class KadNode
	implements MessageHandler
{
	private KeybasedRouting kbrNode;	// The KAD node itself
	private InetAddress localAddress;	// Listening on this address
	private final int port;				// UDP port to listen on
	private int keySize;				// Number of bytes in key used for network identification
	private int bucketSize;				// FIXME: don't know what this is for
	private static final int DEFAULT_PORT = 5000;
	private static final int DEFAULT_KEYSIZE = 16;
	private static final int DEFAULT_BUCKETSIZE = 16;
	
		
	public KadNode() {
		Logger.get().info("Configuring KAD node");
		// Configure
		Properties config = null;
		try {
			config = Configuration.get("kad");
		} catch (IOException e) {
			Logger.get().warning("Can't read KAD configuration; using default settings.");
		}
		
		int port = DEFAULT_PORT;
		int keySize = DEFAULT_KEYSIZE;
		int bucketSize = DEFAULT_BUCKETSIZE;
		
		if (config != null) {
			String szKadPort = config.getProperty("port");
			try {
				port = Integer.parseInt(szKadPort);
			} catch (NumberFormatException e) {
				Logger.get().warning(String.format("[KAD:port] (%s) is not valid integer; using default value (%s)",
				                                   szKadPort, DEFAULT_PORT));
			}
			String szKeySize = config.getProperty("key_size");
			try {
				keySize = Integer.parseInt(szKeySize);
			} catch (NumberFormatException e) {
				Logger.get().warning(String.format("[KAD:key_size] (%s) is not valid integer; using default value (%s)",
				                                   szKeySize, DEFAULT_PORT));
			}
			String szBucketSize = config.getProperty("bucket_size");
			try {
				bucketSize = Integer.parseInt(szBucketSize);
			} catch (NumberFormatException e) {
				Logger.get().warning(String.format("[KAD:bucket_size] (%s) is not valid integer; using default value (%s)",
				                                   szBucketSize, DEFAULT_PORT));
			}
		}
		
		
		 * FIXME: Temporary testing on two machines! Remove this alternate case
		 
		if (System.getProperty("os.name").equals("Mac OS X")) {
			this.port = 5001;
		} else {
			this.port = port;
		}
		this.keySize = keySize;
		this.bucketSize = bucketSize;
	}
	
	
	 * Begin listening for UDP traffic from other KAD nodes
	 
	public final void start() throws IOException {
		Logger.get().info("Starting KAD node");
		
		// Load KadNet module and create an instance
		Injector injector = Guice.createInjector(new KadNetModule()
			.setProperty("openkad.keyfactory.keysize", String.valueOf(this.keySize))
			.setProperty("openkad.bucket.kbuckets.maxsize", String.valueOf(this.bucketSize))
			.setProperty("openkad.seed", String.valueOf(this.port))
			.setProperty("openkad.net.udp.port", String.valueOf(this.port)));
		
		this.kbrNode = injector.getInstance(KeybasedRouting.class);
		try {
			this.kbrNode.create();
		} catch (IOException e) {
			Logger.get().log(Level.SEVERE, String.format("KAD node couldn't start on port %s: %s",
			                                             this.port, e.getMessage()));
			throw e;
		}
		
		// Register event handling
		this.kbrNode.register("mapreduce", this);
		
		// Find address of local outwardly-exposed interface
		final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		// Of all interfaces,
		while (interfaces.hasMoreElements()) {
			// find all addresses
			final NetworkInterface iface = interfaces.nextElement();
			for (Enumeration<InetAddress> addresses = iface.getInetAddresses(); 
					addresses.hasMoreElements();) {
				// and pick the first non-loopback IPV4 address
				InetAddress addr = addresses.nextElement();
				if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
					this.localAddress = addr;
				}
			}
		}
		if (this.localAddress == null) {
			Logger.get().warning("Couldn't find address of local interface");
			// This should default to 127.0.0.1 on most machines
			this.localAddress = this.kbrNode.getLocalNode().getInetAddress();
		}
		
		Logger.get().info(String.format("KAD node running on %s:%d", 
		                                this.getLocalAddress().getHostAddress(), 
		                                this.port));
	}
	
	
	 * Attempt to connect to another node
	 
	public final boolean join(final InetAddress address, final int port) throws URISyntaxException {
		final String szConnect = String.format("openkad.udp://%s:%d/", address.getHostAddress(), port);
		Logger.get().info("KAD node attempting to join ".concat(szConnect));
		try {
			this.kbrNode.join(Arrays.asList(new URI(szConnect)));
			Logger.get().info("Connected to KAD node at ".concat(szConnect));
			return true;
		} catch (IllegalStateException e) {
			Logger.get().log(Level.WARNING, "KAD node at ".concat(szConnect).concat(" is not responding."), e);
			return false;
		}
	}
	
	
	 * Locate an appropriate node by `key` and send `data` to it, returning the response.
	 
	public final Future<Serializable> sendChunk(final DistributedChunk chunk) throws IOException {
		final Key chunkKey = new Key(chunk.getChunkHeader().idHash);
		// Find nodes responsible for key
		Logger.get().info("Finding nodes for key ".concat(chunkKey.toString()));
		final List<Node> nodes = this.kbrNode.findNode(chunkKey);
	
		if (nodes.size() == 0) {
			Logger.get().severe("No node found for chunk ".concat(chunkKey.toString()));
			return null;
		}
		
		// Send chunk to nodes
		final Node destNode = nodes.get(0);
		final byte[] data = new Request(Request.Type.FilePart, chunk.getBytes()).getBytes();
		Logger.get().info(String.format("Sending %s bytes to %s",
		                                data.length, destNode.getInetAddress().getHostAddress())); 
		Future<Serializable> response = this.kbrNode.sendRequest(destNode, "mapreduce", data);
		Logger.get().info("Chunk sent");
		
		return response;
	}
	
	public final DistributedChunk getChunk(final byte[] hash) throws IOException {
		final Key chunkKey = new Key(hash);
		// Find nodes responsible for key
		Logger.get().info("Finding nodes for key ".concat(chunkKey.toString()));
		final List<Node> nodes = this.kbrNode.findNode(chunkKey);
	
		if (nodes.size() == 0) {
			Logger.get().severe("No node found for chunk ".concat(chunkKey.toString()));
			return null;
		}
		
		// Get chunk from node
		final Node sourceNode = nodes.get(0);
		final byte[] data = new Request(Request.Type.Send, hash).getBytes();
		Future<Serializable> response = this.kbrNode.sendRequest(sourceNode, "mapreduce", data);
		
		try {
			Response resp = new Response((byte[])response.get());
			return new DistributedChunk(resp.getContent());
		} catch (ExecutionException|InterruptedException e) {
			Logger.get().log(Level.SEVERE, "Couldn't get response from node", e);
			return null;
		}
	}
	
	
	 * Accept a message from the network
	 
	@Override
	public final void onIncomingMessage(final Node from, final String tag, 
	                             final Serializable content) {
		System.out.println("Node got message: ".concat(content.toString()));
	}
	
	 * Respond to a request from the network
	 
	@Override
	public final Serializable onIncomingRequest(final Node from, final String tag,
	                                     final Serializable content) {
		// Translate request
		final Request request;
		try {
			request = new Request((byte[])content);
		} catch (IOException e) {
			Logger.get().log(Level.SEVERE, "Couldn't parse request", e);
			return null;
		}
		Logger.get().info(String.format("[REQ] %s: %s (%s)", tag, request.getType().toString(),
		                                request.getContent().length));
		// Parse the different request types
		if (request.type == Request.Type.FilePart) {
			// Read the content as a DistributedFileChunk
			final DistributedChunk chunk;
			try {
				chunk = new DistributedChunk(request.getContent());
			} catch (IOException e) {
				Logger.get().log(Level.SEVERE, "Couldn't parse request", e);
				return null;
			}
			
			// TODO: Handle the received chunk here
			Logger.get().info(String.format("Node(%s) sent us chunk %s-%s", 
			                                from.getInetAddress(), chunk.getFileHeader().name,
			                                chunk.getChunkHeader().index));
			
			try {
				final boolean success = DistributedFilesystem.INSTANCE.putChunk(chunk);
			} catch (IOException e) {
				Logger.get().log(Level.SEVERE, "Error saving chunk", e);
			}
			
			// Respond to the request with status information
			// FIXME: Generate random node load
			Random rnd = new Random();
			final ChunkSendResponse response = new ChunkSendResponse(true, rnd.nextInt(5));
			try {
				return new Response(Request.Type.FilePart, Response.Status.OK, tag, response.getBytes()).getBytes();
			} catch (IOException e) {
				Logger.get().log(Level.SEVERE, "Couldn't convert response to bytes; this should never happen", e);
				try {
					final byte[] resp = new Response(Request.Type.FilePart, tag).getBytes();
					Logger.get().info(String.format("Sending %d byte response to %s", resp.length, 
					                                from.getInetAddress().getHostAddress()));
					return resp;
				} catch (IOException e2) {
					Logger.get().log(Level.SEVERE, 
					                 "Couldn't convert response to bytes; this should DEFINITELY never happen", 
					                 e2);
					return null;
				}
			}
		} else if (request.type == Request.Type.Send) {
			final String szFilename = new String(Base64.encodeBase64(request.getContent()));
			byte[] contents;
			try {
				contents = DistributedFilesystem.INSTANCE.getChunkContents(szFilename);
				return new Response(Request.Type.Send, Response.Status.OK, "mapreduce", contents).getBytes();
			} catch (IOException e) {
				Logger.get().log(Level.SEVERE, "Couldn't convert response to bytes; this should never happen", e);
				return null;
			}
		} else {
			try {
				Logger.get().info(String.format("Sending DONT UNDERSTAND to %s",
				                                from.getInetAddress().getHostAddress()));
				return new Response(Request.Type.Send, Response.Status.DONTUNDERSTAND, tag, null).getBytes();
			} catch (IOException e) {
				Logger.get().log(Level.SEVERE, "Couldn't convert response to bytes; this should never happen", e);
				return null;
			}
		}
		return null;
	}
	
	
	 * Getters and setters
	 
	public final KeybasedRouting getRouting() { return this.kbrNode; }
	public final InetAddress getLocalAddress() { return this.localAddress; }
}
*/