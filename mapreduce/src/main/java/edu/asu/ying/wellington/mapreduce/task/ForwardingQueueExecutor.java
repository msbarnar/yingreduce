package edu.asu.ying.wellington.mapreduce.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;

/**
 * {@code ForwardingQueueExecutor} removes tasks from the local {@code Forwarding} queue and places
 * them either in the local {@code Remote} queue (if the local node is <b>not</b> the initial node),
 * or in the {@code Forwarding} queue of one of the immediately connected child nodes.
 */
@Singleton
public final class ForwardingQueueExecutor extends QueueExecutor<Task> {

  private final NodeLocator locator;
  private final TaskService taskService;

  private final QueueExecutor<Task> remoteQueue;

  @Inject
  private ForwardingQueueExecutor(NodeLocator locator,
                                  TaskService taskService,
                                  RemoteQueueExecutor remoteQueue) {
    this.locator = locator;
    this.taskService = taskService;
    this.remoteQueue = remoteQueue;
  }

  /**
   * The queue to which the task is forwarded is chosen as the shortest of QFn, QRn, and QFk where k
   * is all of the neighbor nodes (directly connected peers). If the chosen queue is QFn, no action
   * is taken and the task remains on the forwarding queue.
   */
  @Override
  protected synchronized void process(Task task) {
    Collection<RemoteNode> neighbors = locator.neighbors();

    // Default to forwarding to the local remote queue
    // QFn -> QRn
    // If still null, then ours is the best scheduler to forward to
    RemoteTaskService bestScheduler = null;
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
          bestScheduler = remoteScheduler;
        }
      } catch (RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }

    if (bestScheduler == null) {
      try {
        System.out.println("-> self");
        taskService.accept(task);
      } catch (TaskException e) {
        // TODO: Logging
        e.printStackTrace();
      }
      return;
    }

    // If this forwarding queue is the shortest, put the task back on the queue.
    // QFn -> QFn
    if (maximumBackpressure < 0) {
      offer(task);
      return;
    }

    try {
      // Forward the task to the remote node
      System.out.println("->".concat(bestScheduler.toString()));
      bestScheduler.accept(task);
    } catch (RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
    }
  }
}
