package edu.asu.ying.mapreduce.tasks.scheduling.server;

import edu.asu.ying.mapreduce.tasks.Scheduler;
import edu.asu.ying.mapreduce.tasks.Task;
import edu.asu.ying.mapreduce.tasks.TaskQueue;

/**
 * The {@code ServerTaskScheduler} is responsible for accepting a {@link Task} from another
 * node (or from the local node, if the task was started locally) and queuing it for execution in
 * one of the following queues, deferring to {@code forwarding} if {@code local} is full.
 * <ol>
 *   <li>{@code Local} - tasks are executed directly on the local node.</li>
 *   <li>{@code Forwarding} - tasks are sent to either the local node's {@code remote} queue, or
 *   to the forwarding queue of a random connected node.</li>
 * </ol>
 * Once the scheduler has placed the task in a queue, the task is taken over by that queue's
 * {@link TaskQueueExecutor}.
 */
public class ServerTaskScheduler implements Scheduler {

  // Ql and Qr are bounded, but Qf is just a pipe
  private final TaskQueue localQueue = TaskQueue.create(TaskQueue.MAX_SIZE);
  private final TaskQueue forwardingQueue = TaskQueue.create();
  private final TaskQueue remoteQueue = TaskQueue.create(TaskQueue.MAX_SIZE);

  @Override
  public void schedule(Task task) {
    task.getHistory().push(this.createHistoryEntry());
  }
}
