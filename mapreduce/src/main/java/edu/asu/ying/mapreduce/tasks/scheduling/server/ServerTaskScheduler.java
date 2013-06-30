package edu.asu.ying.mapreduce.tasks.scheduling.server;

import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.net.NodeIdentifier;
import edu.asu.ying.mapreduce.tasks.Scheduler;
import edu.asu.ying.mapreduce.tasks.TaskSchedulingResult;
import edu.asu.ying.mapreduce.tasks.Task;
import edu.asu.ying.mapreduce.tasks.TaskHistory;

/**
 * The {@code ServerTaskScheduler} is responsible for accepting a {@link Task} from another
 * node (or from the local node, if the task was started locally) and queuing it for execution in
 * one of the following queues, deferring to {@code forwarding} if {@code local} is full.
 * <ol>
 *   <li>{@code Local} - tasks are executed directly on the local node.</li>
 *   <li>{@code Forwarding} - tasks are sent to either the local node's {@code remote} queue, or
 *   to the forwarding queue of a random immediately-connected node.</li>
 * </ol>
 * Once the scheduler has placed the task in a queue, the task is taken over by that queue's
 * {@link TaskQueueExecutor}.
 */
public class ServerTaskScheduler implements Scheduler {

  private static final int MAX_QUEUE_SIZE = 1;

  // Ql and Qr are bounded, but Qf is just a pipe
  private final Queue<Task> localQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
  private final Queue<Task> forwardingQueue = new LinkedBlockingQueue<>();
  private final Queue<Task> remoteQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

  // The executors watch the task queues and execute
  private final TaskQueueExecutor localExecutor = new LocalTaskQueueExecutor(this.localQueue);
  private final TaskQueueExecutor forwardingExecutor
      = new ForwardingTaskQueueExecutor(this.forwardingQueue);
  private final TaskQueueExecutor remoteExecutor = new RemoteTaskQueueExecutor(this.remoteQueue);

  /**
   * Accepts a {@link Task} and appends it to the appropriate queue:
   * <ol>
   *   <li>{@code Local} - if the current node is the {@code initial} node, and the queue is not
   *   full.</li>
   *   <li>{@code Forwarding} - if the current node is <b>not</b> the initial node, the task is
   *   placed in this queue. The task will then either be placed in the {@code Remote} queue or, if
   *   it is full, in a child node's {@code Forwarding} queue.</li>
   * </ol>
   */
  @Override
  public TaskSchedulingResult schedule(final Task task) throws RemoteException {

    final TaskSchedulingResult result = new TaskSchedulingResult();

    final TaskHistory.Entry historyEntry = this.createHistoryEntry();

    // If this is the initial node, try to execute the task locally.
    if (this.isInitialNodeFor(task)) {
      historyEntry.setNodeRole(TaskHistory.NodeRole.Initial);
      // Add the task if the local queue is not full
      result.setTaskScheduled(this.queueLocally(task, historyEntry));
      // If the local queue was full, forward the task
      if (!result.isTaskScheduled()) {
        result.setTaskScheduled(this.queueForward(task, historyEntry));
      }
    } else {
      // If this is not the initial node, put the task straight in the forwarding queue to be placed
      // in the remote queue or forwarded to a child node.
      result.setTaskScheduled(this.queueForward(task, historyEntry));
    }

    return result;
  }

  /**
   * Returns {@code true} if this is the initial node (the node carrying the data) for the specified
   * task.
   * </p>
   * This is the initial node if the most recent node to handle the task was the
   * {@code responsible node} for the job.
   */
  private boolean isInitialNodeFor(final Task task) {
    final TaskHistory.Entry lastEntry = task.getHistory().last();
    if (lastEntry == null) {
      // The responsible node didn't append itself to the history before distributing this task from
      // the original job.
      throw new IllegalStateException("Scheduler received task with no responsible node set; don't"
                                      + " know whether this is the initial node, and won't know the"
                                      + " origin of the task if we continue forwarding.");
    }
    return lastEntry.getNodeRole() == TaskHistory.NodeRole.Responsible;
  }

  /**
   * Attempts to queue the {@link Task} in the local queue for execution on the
   * {@code initial node}.
   * @param task the task to be queued.
   * @param historyEntry details regarding the queuing of the task will be indicated in this entry.
   * @return {@code true} if the task was placed on the queue; {@code false} if the queue was full.
   */
  private boolean queueLocally(final Task task, final TaskHistory.Entry historyEntry) {
    final boolean isQueued = this.localQueue.offer(task);
    if (isQueued) {
      // Mark the task as being performed on the initial node
      historyEntry.setSchedulerAction(TaskHistory.SchedulerAction.QueuedLocally);
      task.getHistory().append(historyEntry);
    }
    return isQueued;
  }

  /**
   * Places the {@link Task} in the forwarding queue to be executed remotely.
   * @param task the task to be queued.
   * @param historyEntry details regarding the queuing of the task will be indicated in this entry.
   * @return {@code true}.
   */
  private boolean queueForward(final Task task, final TaskHistory.Entry historyEntry) {
    historyEntry.setSchedulerAction(TaskHistory.SchedulerAction.Forwarded);
    task.getHistory().append(historyEntry);
    return this.forwardingQueue.add(task);
  }

  public final Queue<Task> getLocalQueue() {
    return this.localQueue;
  }

  public final Queue<Task> getForwardingQueue() {
    return this.forwardingQueue;
  }

  public final Queue<Task> getRemoteQueue() {
    return this.remoteQueue;
  }

  private TaskHistory.Entry createHistoryEntry() {
    return new TaskHistory.Entry(this.getLocalNodeUri());
  }

  private NodeIdentifier getLocalNodeUri() {
    // TODO: Get the local node URI
    return null;
  }
}
