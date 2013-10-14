package edu.asu.ying.wellington.mapreduce.task;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;

/**
 * {@code ForwardingQueueExecutor} removes tasks from the local {@code Forwarding} queue and places
 * them either in the local {@code Remote} queue (if the local node is <b>not</b> the initial node),
 * or in the {@code Forwarding} queue of one of the immediately connected child nodes.
 */
public final class ForwardingQueueExecutor extends QueueExecutor<Task> {

  private final LocalNode localNode;

  // Holds an unlimited number of tasks that need to be forwarded to neighbors
  private final BlockingQueue<Task> forwardingQueue = new LinkedBlockingQueue<>();
  private final QueueExecutor<Task> remoteQueue;

  // One task at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();


  public ForwardingQueueExecutor(LocalNode localNode, QueueExecutor<Task> remoteQueue) {
    this.localNode = localNode;
    this.remoteQueue = remoteQueue;
  }

  /**
   * The queue to which the task is forwarded is chosen as the shortest of QFn, QRn, and QFk where k
   * is all of the neighbor nodes (directly connected peers). If the chosen queue is QFn, no action
   * is taken and the task remains on the forwarding queue.
   */
  protected synchronized void process(Task task) {
    final Collection<RemoteNode> neighbors = this.localNode.getNeighbors();

    // Default to forwarding to the local remote queue
    // QFn -> QRn
    // If still null, then ours is the best scheduler to forward to
    RemoteTaskService bestScheduler = null;
    int maximumBackpressure = this.size() - this.remoteQueue.size();

    // Unless one of our neighbors has a lower backpressure
    // Where backpressure is |QFn| - |QFk| {QFk | k E Cn}
    // where Cn is neighbors of this node
    for (RemoteNode node : neighbors) {
      try {
        RemoteTaskService remoteScheduler = node.getTaskService();
        int remoteBackpressure = this.size() - remoteScheduler.getBackpressure();
        if (remoteBackpressure > maximumBackpressure) {
          maximumBackpressure = remoteBackpressure;
          bestScheduler = remoteScheduler;
        }
      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }

    if (bestScheduler == null) {
      try {
        this.localNode.getTaskService().accept(task);
      } catch (TaskException e) {
        // TODO: Logging
        e.printStackTrace();
      }
      return;
    }

    // If this forwarding queue is the shortest, put the task back on the queue.
    // QFn -> QFn
    if (maximumBackpressure < 0) {
      this.offer(task);
      return;
    }

    try {
      // Forward the task to the remote node
      bestScheduler.accept(task);
    } catch (final RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
    }
  }
}
