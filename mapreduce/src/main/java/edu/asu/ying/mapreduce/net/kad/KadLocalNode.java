package edu.asu.ying.mapreduce.net.kad;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.kad.KadSendMessageStream;
import edu.asu.ying.mapreduce.mapreduce.scheduling.SchedulerImpl;
import edu.asu.ying.mapreduce.net.*;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.rmi.ActivatorImpl;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import il.technion.ewolf.kbr.KeybasedRouting;


/**
 *
 */
@Singleton
public final class KadLocalNode
    implements LocalNode {

  // Local kademlia node
  private final KeybasedRouting kbrNode;

  // Incoming mapreduce messages (scheduler requests)
  private final MessageHandler messageIn;
  // Outgoing mapreduce messages
  private final MessageOutputStream messageOut;

  // Provides RMI references to the scheduler
  private final Activator activator;

  // Schedules mapreduce jobs and tasks
  private final Scheduler scheduler;

  @Inject
  private KadLocalNode(final Injector injector) {

    // The local Kademlia node for node discovery
    this.kbrNode = injector.getInstance(KeybasedRouting.class);

    // Set up the message IO
    this.messageIn = new KadMessageHandler(this.kbrNode);
    this.messageOut = new KadSendMessageStream(this.kbrNode);

    // Start the activator to provide Scheduler references
    this.activator = new ActivatorImpl(injector);
    // Start the scheduler with a reference to the local node for finding neighbors
    this.scheduler = new SchedulerImpl(this);
  }

  @Override
  public final void join(final NodeURI bootstrap) throws IOException {
  }

  @Override
  public List<RemoteNode> getNeighbors() {
    return null;
  }

  @Override
  public MessageHandler getIncomingMessageHandler() {
    return this.messageHandler;
  }

  @Override
  public Scheduler getScheduler() throws RemoteException {
    return this.activator.getReference(this.scheduler, null);
  }

  @Override
  public NodeURI getNodeURI() {
    return null;
  }
}
