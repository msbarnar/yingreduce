package edu.asu.ying.mapreduce.net.kad;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import edu.asu.ying.mapreduce.common.event.EventHandler;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.io.Channel;
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

  private final Channel networkChannel;

  @Inject
  private KadLocalNode(final Injector injector,
                       final KeybasedRouting kbrNode,
                       final Channel networkChannel) {

    // The local Kademlia node for node discovery
    this.kbrNode = kbrNode;

    // Start the remote to provide Scheduler references
    this.activator = new ActivatorImpl(injector);
    // Start the scheduler with a reference to the local node for finding neighbors
    this.scheduler = new SchedulerImpl(this);

    this.networkChannel = networkChannel;

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
  public Activator getActivator() {
    return this.activator;
  }

  @Override
  public NodeURI getNodeURI() {
    return null;
  }

  private void bindRequestHandlers() {
    // Register to receive NodeProxyRequest messages
    this.networkChannel.getIncomingMessageHandler().getIncomingMessageEvent().attach(
        FilterClass.is(NodeProxyRequest.class), this);
  }

  /**
   * Receives a {@link NodeProxyRequest} message from the incoming message handler.
   *
   * @return true, so that it always remains bound to the message event.
   */
  @Override
  public boolean onEvent(final @Nonnull Object sender, final @Nullable Message request) {
    if (request == null) {
      return true;
    }
    Message response = this.processRequest((NodeProxyRequest) request);
    try {
      this.networkChannel.sendMessage(response);
    } catch (final IOException e) {
      // TODO: logging
      e.printStackTrace();
    }

    // Always return true to stay bound to the event
    return true;
  }

  private NodeProxyResponse processRequest(final NodeProxyRequest request) {
    final NodeProxyResponse response = NodeProxyResponse.inResponseTo(request);

    // Bind the proxy to the local scheduler
    try {
      final NodeProxy proxyInstance = NodeProxyImpl.createProxyTo(this);
      response.setInstance(proxyInstance);

    } catch (final RemoteException e) {
      response.setException(e);
    }

    return response;
  }
}
