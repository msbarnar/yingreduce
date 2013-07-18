package edu.asu.ying.mapreduce.net.kad;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.io.IOException;
import java.util.List;

import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.RemoteNode;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.messaging.activator.ActivatorRequestHandler;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.rmi.ServerActivator;
import edu.asu.ying.mapreduce.table.Table;
import edu.asu.ying.mapreduce.table.TableID;
import edu.asu.ying.mapreduce.task.scheduling.Scheduler;
import il.technion.ewolf.kbr.KeybasedRouting;


/**
 *
 */
@Singleton
public final class KadLocalNode
    implements LocalNode {

  private final KeybasedRouting kbrNode;

  private final Scheduler localScheduler;

  // Singleton Activator instance
  private final Activator activatorInstance;

  @Inject
  private KadLocalNode(final Injector injector,
                       final KeybasedRouting kbrNode) {

    this.activatorInstance = new ServerActivator(injector);
    this.kbrNode = kbrNode;
    this.localScheduler = new LocalScheduler
  }

  @Override
  public void bind() {
  }

  @Override
  public final void join(final NodeURI bootstrap) throws IOException {
  }

  @Override
  public final Activator getActivator() {
    return this.activatorInstance;
  }

  @Override
  public List<RemoteNode> getNeighbors() {
    return null;
  }

  @Override
  public MessageHandler getIncomingMessageHandler() {
    return null;
  }

  @Override
  public Scheduler getScheduler() {
    return null;
  }

  @Override
  public Table getTable(TableID id) {
    return null;
  }

  @Override
  public NodeURI getNodeURI() {
    return null;
  }
}
