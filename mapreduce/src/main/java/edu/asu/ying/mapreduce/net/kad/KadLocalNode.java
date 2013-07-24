package edu.asu.ying.mapreduce.net.kad;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.common.event.EventHandler;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.mapreduce.scheduling.SchedulerImpl;
import edu.asu.ying.mapreduce.net.*;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.rmi.remote.NodeProxy;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.rmi.ActivatorImpl;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.rmi.remote.NodeProxyImpl;
import edu.asu.ying.mapreduce.rmi.remote.NodeProxyRequest;
import edu.asu.ying.mapreduce.rmi.remote.NodeProxyResponse;
import il.technion.ewolf.kbr.KeybasedRouting;


/**
 *
 */
@Singleton
public final class KadLocalNode
    implements LocalNode, EventHandler<Message> {

  // Local kademlia node
  private final KeybasedRouting kbrNode;

  // Provides RMI references to the scheduler
  private final Activator activator;

  // Schedules mapreduce jobs and tasks
  private final Scheduler scheduler;

  private final MessageHandler incomingMessageHandler;
  private final MessageOutputStream sendMessageStream;

  @Inject
  private KadLocalNode(final Injector injector,
                       final KeybasedRouting kbrNode,
                       final MessageHandler incomingMessageHandler,
                       @SendMessageStream final MessageOutputStream sendMessageStream) {

    // The local Kademlia node for node discovery
    this.kbrNode = kbrNode;

    // Start the remote to provide Scheduler references
    this.activator = new ActivatorImpl(injector);
    // Start the scheduler with a reference to the local node for finding neighbors
    this.scheduler = new SchedulerImpl(this);

    this.incomingMessageHandler = incomingMessageHandler;
    this.sendMessageStream = sendMessageStream;

    this.bindRequestHandlers();
  }

  @Override
  public final void join(final NodeURL bootstrap) throws IOException {
    try {
      this.kbrNode.join(Arrays.asList(bootstrap.toURI()));

    } catch (final IllegalStateException e) {
      throw new NodeNotFoundException(bootstrap);
    } catch (final URISyntaxException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<NodeProxy> getNeighbors() {
    return null;
  }

  /**
   * The local node returns a concrete reference to the scheduler.
   */
  @Override
  public Scheduler getScheduler() {
    return this.scheduler;
  }

  @Override
  public NodeURI getNodeURI() {
    return null;
  }

  private void bindRequestHandlers() {
    // Register to receive NodeProxyRequest messages
    this.incomingMessageHandler.getIncomingMessageEvent().attach(
        FilterClass.is(NodeProxyRequest.class), this);
  }

  /**
   * Receives a {@link NodeProxyRequest} message from the incoming message handler.
   *
   * @return true, so that it always remains bound to the message event.
   */
  @Override
  public boolean onEvent(final Object sender, final @Nullable Message request) {
    if (request == null) {
      return true;
    }
    Message response = this.processRequest((NodeProxyRequest) request);
    try {
      this.sendMessageStream.write(response);
    } catch (final IOException e) {
      // TODO: logging
      e.printStackTrace();
    }

    // Always return true to stay bound to the event
    return true;
  }

  private final NodeProxyResponse processRequest(final NodeProxyRequest request) {
    final NodeProxyResponse response = NodeProxyResponse.inResponseTo(request);

    // Bind the proxy to the local scheduler
    final NodeProxy proxyInstance = new NodeProxyImpl(this);
    final NodeProxy proxy = (NodeProxy) UnicastRemoteObject.exportObject(proxyInstance);
    try {
      // Export the RMI Remote proxy and return it in the message
      instance = (Activator) UnicastRemoteObject.exportObject(this.serverActivator,
                                                              8000 + (new Random()).nextInt(2000));
      response.setInstance(instance);

    } catch (final RemoteException e) {
      response.setException(e);
    }

    return response;
  }
}
