package edu.asu.ying.mapreduce.channels.kad;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import edu.asu.ying.mapreduce.messaging.MessageDispatch;
import edu.asu.ying.mapreduce.messaging.io.MessageOutputStream;
import edu.asu.ying.mapreduce.messaging.SendMessageStream;
import edu.asu.ying.mapreduce.messaging.SimpleMessageDispatch;
import edu.asu.ying.mapreduce.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.activator.kad.KadServerActivator;
import il.technion.ewolf.kbr.*;
import il.technion.ewolf.kbr.openkad.KadNetModule;

import java.io.IOException;
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
	 * Singleton {@link MessageDispatch} provider.
	 */
	private enum MessageDispatchProvider {
		INSTANCE;
		private final MessageDispatch dispatch;

		private MessageDispatchProvider() {
			this.dispatch = new SimpleMessageDispatch();
		}
	}

	@Override
	protected void configure() {
		bind(Activator.class).to(KadServerActivator.class);
	}

	/**
	 * Provides a {@link KadMessageHandler} that listens to the local node and writes to a {@link MessageDispatch}.
	 */
	@Provides
	private final KadMessageHandler provideMessageHandler() {
		final Injector injector = Guice.createInjector(this);
		return new KadMessageHandler(injector.getInstance(KeybasedRouting.class),
		                             injector.getInstance(MessageDispatch.class));
	}

	/**
	 * Provides an instance of {@link MessageDispatch} for objects to register to receive specific messages from the
	 * network.
	 */
	@Provides
	private final MessageDispatch provideMessageDispatch() {
		return MessageDispatchProvider.INSTANCE.dispatch;
	}

	/**
	 * Provides an instance of {@link KeybasedRouting} to send and receive channels for their underlying network
	 * communication.
	 */
	@Provides
	private final KeybasedRouting provideKeybasedRouting() {
		return KadNodeProvider.INSTANCE.kadNode;
	}

	/**
	 * Provides a message output stream that is tied to a {@link KadSendChannel} such that messages written to the
	 * stream are sent into the network.
	 */
	@Provides @SendMessageStream
	private final MessageOutputStream provideSendMessageStream() {
		Injector injector = Guice.createInjector(this);
		return new KadSendChannel(injector.getInstance(KeybasedRouting.class));
	}
}
