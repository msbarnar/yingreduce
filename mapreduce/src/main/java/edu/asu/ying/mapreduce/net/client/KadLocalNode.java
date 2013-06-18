package edu.asu.ying.mapreduce.net.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.asu.ying.mapreduce.messaging.MessageHandler;
import edu.asu.ying.mapreduce.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.net.kad.KademliaNetwork;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.activator.kad.KadServerActivator;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.openkad.KadNetModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 *
 */
@Singleton
public class KadLocalNode
	implements LocalNode
{
	// Singleton Kademlia routing
	private KeybasedRouting kadNode;
	// Singleton message handlers, by scheme
	private final Map<String, KadMessageHandler> messageHandlers = new HashMap<>();
	// Singleton Activator instance
	private Activator activatorInstance;


	@Inject
	private KadLocalNode() {
	}

	@Override
	public void join(final ResourceIdentifier bootstrap) throws IOException {
	}

	@Override
	public final MessageHandler getMessageHandler(final String scheme) {
		KadMessageHandler handler = this.messageHandlers.get(scheme);
		if (handler == null) {
			synchronized (this.messageHandlers) {
				if (this.messageHandlers.get(scheme) == null) {
					final Injector injector = Guice.createInjector(new KademliaNetwork());
					handler = injector.getInstance(KadMessageHandler.class);
					handler.bind(scheme);
					this.messageHandlers.put(scheme, handler);
				}
			}
		}
		return handler;
	}

	@Override
	public final Activator getActivator() {
		if (this.activatorInstance == null) {
			synchronized (this.activatorInstance) {
				if (this.activatorInstance == null) {
					this.activatorInstance = new KadServerActivator();
				}
			}
		}
		return this.activatorInstance;
	}
}
