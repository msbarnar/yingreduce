package edu.asu.ying.mapreduce.mapreduce.queuing;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.scheduling.RemoteScheduler;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.p2p.RemoteNode;

/**
 * {@code ForwardingTaskQueue} removes tasks from the local {@code Forwarding} queue and
 * places them either in the local {@code Remote} queue (if the local node is <b>not</b> the initial
 * node), or in the {@code Forwarding} queue of one of the immediately connected child nodes.
 */
public final class ForwardingTaskQueue implements TaskQueue {

  private final LocalScheduler scheduler;
  private final LocalNode localNode;

  // Accepts tasks that are forwarded to the local node as remote tasks (data elsewhere).
  private final TaskQueue remoteQueue;

  // Holds an unlimited number of tasks that need to be forwarded to neighbors
  private final BlockingQueue<Task> forwardingQueue = new LinkedBlockingQueue<>();
  // One task at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();


  public ForwardingTaskQueue(final LocalScheduler scheduler,
                             final LocalNode localNode) {

    this.scheduler = scheduler;
    this.remoteQueue = scheduler.getRemoteQueue();
    this.localNode = localNode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final synchronized void start() {
    this.threadPool.submit(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean offer(final Task task) {
    return this.forwardingQueue.offer(task);
  }

  /**
   * {@inheritDoc}
   */
  public final int size() {
    return this.forwardingQueue.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final synchronized void run() {
    // Forward tasks forever
    this.threadPool.submit(this);

    Task task = null;
    try {
      // Blocks until available
      task = this.forwardingQueue.take();
    } catch (final InterruptedException e) {
      // TODO: Logging
      e.printStackTrace();
    }

    if (task == null) {
      return;
    }

    if (task.isAtInitialNode()) {
      // Don't put tasks on the initial node's remote queue; that doesn't make any sense.
      this.forwardTask(task);
    } else {
      // Attempt to put the mapreduce in the local remote queue, unless it is full
      if (this.remoteQueue.offer(task)) {
        System.out.println(String.format("[%s] Task: accepted into remote queue",
                                         this.localNode.getIdentifier()));
      } else {
        this.forwardTask(task);
      }
    }
  }

  private synchronized void forwardTask(final Task task) {
    final List<RemoteNode> neighbors = this.localNode.getNeighbors();

    // Default to forwarding to the local remote queue
    int minimumBackpressure = this.remoteQueue.size();
    RemoteScheduler bestScheduler = this.scheduler.getProxy();

    // Unless one of our neighbors has a lower backpressure
    // TODO: adjust backpressure calculation per weina's suggestions
    for (final RemoteNode node : neighbors) {
      try {
        final RemoteScheduler remoteScheduler = node.getScheduler();
        final int remoteBackpressure = remoteScheduler.getBackpressure();
        if (remoteBackpressure < minimumBackpressure) {
          minimumBackpressure = remoteBackpressure;
          bestScheduler = remoteScheduler;
        }

      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }

    if (bestScheduler == null) {
      throw new IllegalStateException("Couldn't forward task: no connected nodes");
    }

    try {
      System.out.println(String.format("Forwarding task %s to node %s",
                                       task.getId(), bestScheduler.getNode().getIdentifier()));

      bestScheduler.acceptTaskAsInitialNode(task);
    } catch (final RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
    }
  }
}
