package edu.asu.ying.mapreduce.node.kad;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.mapreduce.scheduling.SchedulerImpl;
import edu.asu.ying.mapreduce.node.*;
import edu.asu.ying.mapreduce.rmi.node.NodeProxy;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.rmi.ActivatorImpl;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.rmi.node.NodeProxyRequestHandler;
import il.technion.ewolf.kbr.KeybasedRouting;


/**
 *
 */
@Singleton
public final class KadLocalNode
    implements LocalNode {

  // Local kademlia node
  private final KeybasedRouting kbrNode;

  // Provides RMI references to the scheduler
  private final Activator activator;

  // Schedules mapreduce jobs and tasks
  private final Scheduler scheduler;

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

    // Expose this local node to NodeProxy requests via the request handler
    NodeProxyRequestHandler.exposeNodeToChannel(this, networkChannel);

    System.out.println("The local Kademlia node is listening.");
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
}
