package edu.asu.ying.mapreduce.rmi.scheduling.server;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.rmi.scheduling.Scheduler;
import edu.asu.ying.mapreduce.tasks.Task;

/**
 * The {@code ServerTaskScheduler} is responsible for accepting a {@link Task} from another
 * node (or from the local node, if the task was started locally) and queuing it for execution in
 * one of the following queues, deferring to the next if one is full:
 * <ol>
 *   <li>{@code Local} - tasks are executed directly on the local node.</li>
 *   <li>{@code Forwarding} - tasks are sent to either the local node's {@code remote} queue, or
 *   to the forwarding queue of a random connected node.</li>
 *   <li>{@code Remote} - tasks received from another node are executed locally, but the data for
 *   the task exists on another node.</li>
 * </ol>
 * Once the scheduler has placed the task in a queue, the task is taken over by that queue's
 * {@link TaskQueueExecutor}.
 */
public class ServerTaskScheduler implements Scheduler {

  @Override
  public void schedule(Task task) {
  }
}
