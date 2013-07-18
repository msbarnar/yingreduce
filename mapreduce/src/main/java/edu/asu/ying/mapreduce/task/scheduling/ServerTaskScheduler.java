package edu.asu.ying.mapreduce.task.scheduling;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.task.Task;
import edu.asu.ying.mapreduce.task.TaskHistory;
import edu.asu.ying.mapreduce.task.executor.ForwardingTaskQueueExecutor;
import edu.asu.ying.mapreduce.task.executor.TaskQueueExecutor;

/**
 * The {@code ServerTaskScheduler} is responsible for accepting a {@link Task} from another
 * node (or from the local node, if the task was started locally) and queuing it for execution in
 * one of the following queues, deferring to {@code forwarding} if {@code local} is full.
 * <ol>
 *   <li>{@code Local} - task are executed directly on the local node.</li>
 *   <li>{@code Forwarding} - task are sent to either the local node's {@code remote} queue, or
 *   to the forwarding queue of a random immediately-connected node.</li>
 * </ol>
 * Once the scheduler has placed the task in a queue, the task is taken over by that queue's
 * {@link TaskQueueExecutor}.
 */
public class ServerTaskScheduler implements Scheduler {

  private static final int MAX_QUEUE_SIZE = 1;

  // Ql and Qr are bounded, but Qf is just a pipe
  private final BlockingQueue<Task> localQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
  private final BlockingQueue<Task> forwardingQueue = new LinkedBlockingQueue<>();
  private final BlockingQueue<Task> remoteQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

  // The executors watch the task queues and execute
  private final TaskQueueExecutor localExecutor = new LocalTaskQueueExecutor(this.localQueue);
  private final TaskQueueExecutor forwardingExecutor;
  private final TaskQueueExecutor remoteExecutor = new RemoteTaskQueueExecutor(this.remoteQueue);

  @Inject
  private ServerTaskScheduler(final LocalNode localNode) {

    this.localExecutor.start();

    this.forwardingExecutor = new ForwardingTaskQueueExecutor(this,
                                                              this.forwardingQueue,
                                                              this.remoteQueue, localNode);
    this.forwardingExecutor.start();

    this.remoteExecutor.start();
  }

  public final int getBackpressure() {
    return this.remoteQueue.size() + this.forwardingQueue.size();
  }

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
  public TaskSchedulingResult addTask(final Task task) throws RemoteException {

    final TaskSchedulingResult result = new TaskSchedulingResult();

    final TaskHistory.Entry historyEntry = this.createHistoryEntry();

    // If this is the initial node, try to execute the task locally.
    if (task.isCurrentlyAtInitialNode()) {
      historyEntry.setNodeRole(TaskHistory.NodeRole.Initial);
      // Add the task if the local queue is not full
      result.setTaskScheduled(this.queueLocally(task, historyEntry));
      // If the local queue was full, forward the task
      if (!result.isTaskScheduled()) {
        result.setTaskScheduled(this.queueForward(task, historyEntry));
      }
    } else {
      // If this is not the initial node, put the task straight in the forwarding queue
      result.setTaskScheduled(this.queueForward(task, historyEntry));
    }

    return result;
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
