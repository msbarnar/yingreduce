package edu.asu.ying.mapreduce.mapreduce.queuing;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.scheduling.RemoteScheduler;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.RemotePeer;

/**
 * {@code ForwardingTaskQueue} removes tasks from the local {@code Forwarding} queue and places them
 * either in the local {@code Remote} queue (if the local node is <b>not</b> the initial node), or
 * in the {@code Forwarding} queue of one of the immediately connected child nodes.
 */
public final class ForwardingTaskQueue implements TaskQueue {

  private final LocalScheduler scheduler;
  private final LocalPeer localPeer;

  // Accepts tasks that are forwarded to the local node as remote tasks (data elsewhere).
  private final TaskQueue remoteQueue;

  // Holds an unlimited number of tasks that need to be forwarded to neighbors
  private final BlockingQueue<Task> forwardingQueue = new LinkedBlockingQueue<>();
  // One task at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();


  public ForwardingTaskQueue(final LocalScheduler scheduler,
                             final LocalPeer localPeer) {

    this.scheduler = scheduler;
    this.remoteQueue = scheduler.getRemoteQueue();
    this.localPeer = localPeer;
  }

  /**
   * {@inheritDoc}
   */

  public final void start() {
    this.threadPool.submit(this);
  }

  /**
   * {@inheritDoc}
   */

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

  public final void run() {
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

    this.forwardTask(task);
  }

  /**
   * The queue to which the task is forwarded is chosen as the shortest of QFn, QRn, and QFk where k
   * is all of the child nodes (directly connected peers). If the chosen queue is QFn, no action is
   * taken and the task remains on the forwarding queue.
   */
  private synchronized void forwardTask(final Task task) {
    final Collection<RemotePeer> neighbors = this.localPeer.getNeighbors();

    // Default to forwarding to the local remote queue
    // QFn -> QRn
    int maximumBackpressure = this.size() - this.remoteQueue.size();
    RemoteScheduler bestScheduler = null;//FIXME: BROKEN FOR TESTING this.scheduler.getProxy();

    // Unless one of our neighbors has a lower backpressure
    for (final RemotePeer node : neighbors) {
      try {
        // FIXME: BROKEN FOR TESTING
        /*final RemoteScheduler remoteScheduler = node.getScheduler();
        final int remoteBackpressure = this.size() - remoteScheduler.getBackpressure();
        if (remoteBackpressure > maximumBackpressure) {
          maximumBackpressure = remoteBackpressure;
          bestScheduler = remoteScheduler;
        }*/
        throw new RemoteException();

      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }

    if (bestScheduler == null) {
      throw new IllegalStateException("[Forward] Failed; no connected nodes");
    }

    // If this forwarding queue is the shortest, put the task back on the queue.
    // QFn -> QFn
    if (maximumBackpressure < 0) {
      this.offer(task);
      return;
    }

    try {
      // TODO: Logging
      /*System.out.println(String.format("[Forward] %s: %s -> %s",
                                       task.getId(), this.localPeer.getIdentifier(),
                                       bestScheduler.getNode().getIdentifier()));*/

      // Forward the task to the remote node
      bestScheduler.acceptTask(task);
    } catch (final RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
    }
  }
}
