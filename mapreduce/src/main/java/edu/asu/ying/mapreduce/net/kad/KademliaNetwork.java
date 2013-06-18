package edu.asu.ying.mapreduce.net.kad;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import edu.asu.ying.mapreduce.common.events.FilteredValueEvent;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.kad.KadSendMessageStream;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.net.client.KadLocalNode;
import edu.asu.ying.mapreduce.net.client.LocalNode;
import edu.asu.ying.mapreduce.net.resources.ResourceMessageEvent;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.activator.ServerActivator;
import edu.asu.ying.mapreduce.rmi.activator.kad.KadServerActivator;
import edu.asu.ying.mapreduce.rmi.activator.kad.RemoteTest;
import il.technion.ewolf.kbr.*;
import il.technion.ewolf.kbr.openkad.KadNetModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * The {@code KademliaNetwork} wires all of the high-level operations
 * (e.g. {@link edu.asu.ying.mapreduce.net.resources.client.RemoteResourceFinder}) to the underlying Kademlia network classes.
 */
public final class KademliaNetwork
	extends AbstractModule
{
	// Singleton local node
	private LocalNode localNode;

	/**
	 * Singleton {@link KeybasedRouting} provider for all Kademlia traffic
	 */
	private enum KadNodeProvider {
		INSTANCE;
		private final KeybasedRouting kadNode;

		private KadNodeProvider() {
			final int port = 5000 + (new Random()).nextInt(1000);
			final Injector injector = Guice.createInjector(new KadNetModule()
                                         .setProperty("openkad.keyfactory.keysize", String.valueOf(20))
                                         .setProperty("openkad.bucket.kbuckets.maxsize", String.valueOf(20))
                                         .setProperty("openkad.seed", String.valueOf(port))
                                         .setProperty("openkad.net.udp.port", String.valueOf(port)));
			this.kadNode = injector.getInstance(KeybasedRouting.class);
			try {
				this.kadNode.create();
			} catch (final IOException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}

	@Override
	protected void configure() {
		bind(MessageOutputStream.class).annotatedWith(SendMessageStream.class).to(KadSendMessageStream.class);
		bind(RemoteTest.class).to(KadServerActivator.RemoteTestImpl.class);
	}

	/**
	 * Provides an instance of {@link KeybasedRouting} to send and receive channels for their underlying network
	 * communication.
	 */
	@Provides
	private final KeybasedRouting provideKeybasedRouting() {
		return KadNodeProvider.INSTANCE.kadNode;
	}

	@Provides
	private final LocalNode provideLocalNode() {
		if (this.localNode == null) {
			synchronized (this.localNode) {
				if (this.localNode == null) {
					final Injector injector = Guice.createInjector(this);
					this.localNode = injector.getInstance(KadLocalNode.class);
				}
			}
		}
		return this.localNode;
	}
	/*
	 * Message handler providers
	 */
	@Provides
	@ResourceMessageEvent
	private final FilteredValueEvent<Message> provideResourceMessageEvent() {
		return this.provideLocalNode().getMessageHandler("resource").getIncomingMessageEvent();
	}}
