package edu.asu.ying.mapreduce.net.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.net.kad.KademliaNetwork;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;
import edu.asu.ying.mapreduce.net.resources.server.ResourceRequestHandler;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.rmi.kad.KadServerActivator;


/**
 *
 */
@Singleton
public class KadLocalNode
    implements LocalNode {

  // Singleton message handlers, by scheme
  private final Map<String, KadMessageHandler> messageHandlers = new HashMap<>();
  private final Provider<MessageHandler> messageHandlerProvider;

  // Singleton Activator instance
  private Activator activatorInstance;
  private final Object activatorInstanceLock = new Object();
  // Receives Activator requests and returns activators
  private final Provider<ResourceRequestHandler> activatorRequestHandlerProvider;


  @Inject
  private KadLocalNode(final Provider<MessageHandler> messageHandlerProvider,
                       final @Named("activator")
                       Provider<ResourceRequestHandler> activatorRequestHandlerProvider) {

    this.messageHandlerProvider = messageHandlerProvider;
    this.activatorRequestHandlerProvider = activatorRequestHandlerProvider;
  }

  /**
   * Binds the message handling services to the network.
   * </p>
   * It is important that this is called after the {@link LocalNode} is fully constructed because
   * the message handling services create a dependency cycle with the local node's message handlers.
   * </p>
   * The local node is injected as a singleton, so once it is constructed the cyckle is broken.
   */
  public void bind() {
    // Start the activator request handler
    this.activatorRequestHandlerProvider.get();
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
      synchronized (this.activatorInstanceLock) {
        if (this.activatorInstance == null) {
          this.activatorInstance = new KadServerActivator();
        }
      }
    }
    return this.activatorInstance;
  }
}
