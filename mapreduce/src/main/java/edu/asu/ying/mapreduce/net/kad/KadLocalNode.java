package edu.asu.ying.mapreduce.net.kad;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.io.IOException;

import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.resource.ResourceIdentifier;
import edu.asu.ying.mapreduce.net.resource.server.ResourceRequestHandler;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.rmi.ServerActivator;
import il.technion.ewolf.kbr.KeybasedRouting;


/**
 *
 */
@Singleton
public class KadLocalNode
    implements LocalNode {

  private final KeybasedRouting kbrNode;

  private final MessageHandler incomingMessageHandler;

  // Singleton Activator instance
  private Activator activatorInstance;
  private final Object activatorInstanceLock = new Object();
  // Receives Activator requests and returns activators
  private final Provider<ResourceRequestHandler> activatorRequestHandlerProvider;


  @Inject
  private KadLocalNode(final KeybasedRouting kbrNode,
                       final MessageHandler incomingMessageHandler,
                       final @Named("activator")
                       Provider<ResourceRequestHandler> activatorRequestHandlerProvider) {

    this.kbrNode = kbrNode;
    this.incomingMessageHandler = incomingMessageHandler;
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
  public final Activator getActivator() {
    if (this.activatorInstance == null) {
      synchronized (this.activatorInstanceLock) {
        if (this.activatorInstance == null) {
          this.activatorInstance = new ServerActivator();
        }
      }
    }
    return this.activatorInstance;
  }
}
