package edu.asu.ying.mapreduce.rmi.scheduling;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.tasks.Task;

/**
 * {@code Scheduler} is responsible for the allocation of {@code map} and {@code reduce} tasks.
 * </p>
 * The scheduler receives tasks from other nodes and forwards them to one of the following queues
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

  void schedule(final Task task);
}
