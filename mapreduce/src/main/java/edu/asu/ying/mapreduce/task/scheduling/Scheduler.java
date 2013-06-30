package edu.asu.ying.mapreduce.task.scheduling;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.task.Task;

/**
 * {@code Scheduler} is responsible for the allocation of {@code map} and {@code reduce} task.
 * </p>
 * The scheduler receives task from other nodes and forwards them to one of the following queues
 * based on the given criteria:
 * <ol>
 *   <li>{@code local} - if this queue is not full, the task is performed locally.</li>
 *   <li>{@code remote} - if another node forwarded the task to this node, and the remote queue is
 *   not full, the task is performed as a "remote task;" that is, the task belongs to another node,
 *   but is executed by this node.</li>
 *   <li>{@code forward} - if the local queue is full, or the task is a remote task and the remote
 *   queue is full, the task is placed in this queue to be forwarded to another node.</li>
 * </ol>
 */
public interface Scheduler extends Remote {

  TaskSchedulingResult schedule(final Task task) throws RemoteException;

  /**
   * Returns a value equal to or greater than {@code 0} that specifies this scheduler's resistance
   * to having tasks forwarded to it. A high return value indicates that this node is already
   * burdoned by many forwarded tasks. A value of {@code 0} indicates that this node is handling
   * no forwarded tasks.
   * </p>
   * Forwarding nodes should forward tasks in a way that minimizes the backpressure at each of its
   * child nodes.
   */
  int getBackpressure();
}
