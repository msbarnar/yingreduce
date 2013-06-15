package edu.asu.ying.mapreduce.channels.kad;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import edu.asu.ying.mapreduce.messaging.MessageDispatch;
import edu.asu.ying.mapreduce.messaging.io.MessageOutputStream;
import edu.asu.ying.mapreduce.messaging.SendMessageStream;
import edu.asu.ying.mapreduce.messaging.SimpleMessageDispatch;
import edu.asu.ying.mapreduce.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.kad.KadLocalNode;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.activator.kad.KadServerActivator;
import edu.asu.ying.mapreduce.rmi.resource.ResourceFinder;
import edu.asu.ying.mapreduce.rmi.resource.SyncResourceFinder;
import il.technion.ewolf.kbr.*;
import il.technion.ewolf.kbr.openkad.KadNetModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * The {@link KadChannel} constructs the message and channel sink chains necessary to connect high-level peer
 * operations (e.g. {@link edu.asu.ying.mapreduce.rmi.resource.ResourceFinder} to the underlying Kademlia network stack.
 */
public final class KadChannel
	extends AbstractModule
{
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


	/**
	 * String -> Singleton {@link MessageDispatch} provider.
	 */
	private enum MessageDispatchProvider {
		INSTANCE;
		private final Map<String, MessageDispatch> dispatches = new HashMap<>();

		private final MessageDispatch getDispatch(final String scheme) {
			MessageDispatch dispatch = this.dispatches.get(scheme);
			if (dispatch == null) {
				dispatch = new SimpleMessageDispatch(scheme);
				this.dispatches.put(scheme, dispatch);
			}
			return dispatch;
		}
	}

	@Override
	protected void configure() {
		bind(Activator.class).to(KadServerActivator.class);
		bind(ResourceFinder.class).to(SyncResourceFinder.class);
		bind(LocalNode.class).to(KadLocalNode.class);
		bind(MessageOutputStream.class).annotatedWith(SendMessageStream.class).to(KadSendMessageStream.class);
	}

	/**
	 * Provides an instance of {@link MessageDispatch} for objects to register to receive specific messages from the
	 * network.
	 */
	// TODO: find a way to provide arbitrarily named instances
	@Provides
	@Named("resource")
	private final MessageDispatch provideMessageDispatch(final KeybasedRouting kadNode) {
		return provideSchemedDispatch("resource", kadNode);
	}

	private final MessageDispatch provideSchemedDispatch(final String scheme, final KeybasedRouting kadNode) {
		final MessageDispatch dispatch = MessageDispatchProvider.INSTANCE.getDispatch("resource");
		// Attach a handler to the kad node on behalf of the dispatch
		final KadMessageHandler handler = new KadMessageHandler(scheme, kadNode, dispatch);
		return dispatch;
	}

	/**
	 * Provides an instance of {@link KeybasedRouting} to send and receive channels for their underlying network
	 * communication.
	 */
	@Provides
	private final KeybasedRouting provideKeybasedRouting() {
		return KadNodeProvider.INSTANCE.kadNode;
	}
}
