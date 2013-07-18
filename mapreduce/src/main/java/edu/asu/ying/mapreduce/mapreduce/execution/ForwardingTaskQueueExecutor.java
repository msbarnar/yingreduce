package edu.asu.ying.mapreduce.mapreduce.execution;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.RemoteNode;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskHistory;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;

/**
 * {@code ForwardingTaskQueueExecutor} removes tasks from the local {@code Forwarding} queue and
 * places them either in the local {@code Remote} queue (if the local node is <b>not</b> the initial
 * node), or in the {@code Forwarding} queue of one of the immediately connected child nodes.
 */
public final class ForwardingTaskQueueExecutor implements TaskQueueExecutor {

  private final Scheduler scheduler;
  private final BlockingQueue<Task> forwardingQueue;
  private final BlockingQueue<Task> remoteQueue;
  private final LocalNode localNode;

  // One task at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  public ForwardingTaskQueueExecutor(final Scheduler scheduler,
                                     final BlockingQueue<Task> forwardingQueue,
                                     final BlockingQueue<Task> remoteQueue,
                                     final LocalNode localNode) {

    this.scheduler = scheduler;
    this.forwardingQueue = forwardingQueue;
    this.remoteQueue = remoteQueue;
    this.localNode = localNode;
  }

  @Override
  public void run() {
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

    if (task.isCurrentlyAtInitialNode()) {
      // Don't put tasks on the initial node's remote queue; that doesn't make any sense.
      this.forwardTask(task);
    } else {
      // Attempt to put the mapreduce in the local remote queue, unless it is full
      if (this.remoteQueue.offer(task)) {
        // Override the Forwarded action set by the scheduler, indicating that the mapreduce was
        // accepted as a remote mapreduce
        final TaskHistory.Entry lastEntry = task.getHistory().last();
        if (lastEntry == null) {
          throw new IllegalStateException("Trying to forward task that has no history; every node"
                                          + " after this will not know how to route this task.");
        }
        lastEntry.setSchedulerAction(TaskHistory.SchedulerAction.QueuedRemotely);
      } else {
        this.forwardTask(task);
      }
    }
  }

  private void forwardTask(final Task task) {
    final List<RemoteNode> neighbors = this.localNode.getNeighbors();

    // Default to forwarding to the local remote queue
    int minimumBackpressure = this.remoteQueue.size();
    Scheduler bestScheduler = this.scheduler;

    // Unless one of our neighbors has a lower backpressure
    // TODO: adjust backpressure calculation per weina's suggestions
    for (final RemoteNode node : neighbors) {
      final Scheduler remoteScheduler = node.getScheduler();
      final int remoteBackpressure = remoteScheduler.getBackpressure();
      if (remoteBackpressure < minimumBackpressure) {
        minimumBackpressure = remoteBackpressure;
        bestScheduler = remoteScheduler;
      }
    }

    if (bestScheduler == null) {
      throw new IllegalStateException("Couldn't forward task: no connected nodes");
    }

    try {
      bestScheduler.addTask(task);
    } catch (final RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
    }
  }
}
