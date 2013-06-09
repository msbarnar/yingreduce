/*
 * Daemon.java
 * Starts a KAD node and waits for input from the network, local user, or administration panel.
 */

package edu.asu.ying.mapreduce;

import java.util.logging.Level;

import edu.asu.ying.mapreduce.logging.Logger;
import edu.asu.ying.mapreduce.ui.ObservableProvider;


public enum Daemon
{
	INSTANCE;
	
	private TableServerDaemon tableServer;
	private InterfaceDaemon interfaceDaemon;
	
	private boolean ready = false;
	
	private Daemon() {
		// Start the daemons
		try {
			this.tableServer = new TableServerDaemon();
			// Make the table server available to the interface
			ObservableProvider.INSTANCE.register(this.tableServer);
			this.interfaceDaemon = new InterfaceDaemon();
			this.ready = true;
		} catch (final Throwable e) {
			Logger.get().log(Level.SEVERE, "Exception starting daemon", e);
			if (this.tableServer != null) {
				this.tableServer.stop();
				this.tableServer = null;
			}
			if (this.interfaceDaemon != null) {
				try {
					this.interfaceDaemon.stop();
				} catch (final Exception ex) {
					Logger.get().log(Level.SEVERE, "Exception shutting down interface", ex);
				}
				this.interfaceDaemon = null;
			}
		}
	}
	
	public void run() {
		// Keep daemon threads alive indefinitely
		if (!this.ready) {
			Logger.get().log(Level.SEVERE, "Cancelling daemon start; daemon is not ready.");
			return;
		}
		Logger.get().info("Daemon is running");
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				Logger.get().log(Level.INFO, "Daemon got interrupted", e);
				break;
			}
		}
		
		try {
			this.interfaceDaemon.stop();
		} catch (final Exception e) {
			Logger.get().log(Level.SEVERE, "Exception shutting down interface", e);
		}
		this.tableServer.stop();
	}
	
	/*public void start() throws IOException, URISyntaxException {
		// Make client (outbound) KAD channel
		KadSendChannel kadChannel = new KadSendChannel(new URI("//127.0.0.1:5000"));
		// Get a sink to send messages to the channel
		MessageSink kadSink = kadChannel.getMessageSink();
		// Make a distributed table that sends messages to the KAD channel sink
		ClientTable table = new ClientTable(new TableID("myTable"), kadSink);
		// Make a data source that reads text from a file and sends elements to the table
		FileInputStream fis = new FileInputStream("mytext.txt");
		DelimitedTextSource elementSource = new DelimitedTextSource(fis, table, '\n', '=');
		for (;;) {
			try { 
				elementSource.readNextElement();
			} catch (NoSuchElementException e) {
				break;
			}
		}
		// Send all pages across the network
		table.flushPages();
		
		try {
			HttpServer webInterface = new HttpServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
