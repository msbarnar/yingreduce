package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.remoting.Remote;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 * {@code ForwardingQueueExecutor} removes tasks from the local {@code Forwarding} queue and places
 * them either in the local {@code Remote} queue (if the local node is <b>not</b> the initial
 * node),
 * or in the {@code Forwarding} queue of one of the immediately connected child nodes.
 */
public final class ForwardingQueueExecutor extends QueueExecutor<Task> {

  private static final Logger log = Logger.getLogger(ForwardingQueueExecutor.class.getName());

  private final NodeLocator locator;

  private final QueueExecutor<Task> remoteQueue;

  @Inject
  private ForwardingQueueExecutor(NodeLocator locator,
                                  @Remote QueueExecutor<Task> remoteQueue) {
    this.locator = locator;
    this.remoteQueue = remoteQueue;
  }

  /**
   * The queue to which the task is forwarded is chosen as the shortest of QFn, QRn, and QFk where
   * k
   * is all of the neighbor nodes (directly connected peers). If the chosen queue is QFn, no action
   * is taken and the task remains on the forwarding queue.
   */
  @Override
  protected synchronized void process(Task task) {
    Collection<RemoteNode> neighbors = locator.neighbors();

    // Default to forwarding to the local remote queue
    // QFn -> QRn
    // If still null, then ours is the best scheduler to forward to
    RemoteTaskService bestNeighbor = null;
    int maximumBackpressure = size() - remoteQueue.size();

    // Unless one of our neighbors has a lower backpressure
    // Where backpressure is |QFn| - |QFk| {QFk | k E Cn}
    // where Cn is neighbors of this node
    for (RemoteNode node : neighbors) {
      try {
        RemoteTaskService remoteScheduler = node.getTaskService();
        int remoteBackpressure = size() - remoteScheduler.getBackpressure();
        if (remoteBackpressure > maximumBackpressure) {
          maximumBackpressure = remoteBackpressure;
          bestNeighbor = remoteScheduler;
        }
      } catch (RemoteException e) {
        log.log(Level.WARNING, "Remote exception getting backpressure from remote scheduler", e);
      }
    }

    // We are the best queue to be in
    if (bestNeighbor == null) {
      // If this forwarding queue is the shortest, put the task back on the queue.
      // QFn -> QFn
      if (maximumBackpressure < 0) {
        add(task);
      } else {
        // Put the task in the remote queue
        remoteQueue.add(task);
      }
    } else {
      // Forward the task to the remote node
      try {
        bestNeighbor.accept(task);
      } catch (RemoteException e) {
        log.log(Level.WARNING, "Remote exception forwarding task to remote node", e);
      }
    }
  }
}
