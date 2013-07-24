package edu.asu.ying.mapreduce.mapreduce.scheduling;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskSchedulingResult;

/**
 * {@code Scheduler} is responsible for the allocation of {@code map} and {@code reduce} mapreduce.
 * </p>
 * The scheduler receives mapreduce from other nodes and forwards them to one of the following queues
 * based on the given criteria:
 * <ol>
 *   <li>{@code local} - if this queue is not full, the mapreduce is performed locally.</li>
 *   <li>{@code remote} - if another node forwarded the mapreduce to this node, and the remote queue is
 *   not full, the mapreduce is performed as a "remote mapreduce;" that is, the mapreduce belongs to another node,
 *   but is executed by this node.</li>
 *   <li>{@code forward} - if the local queue is full, or the mapreduce is a remote mapreduce and the remote
 *   queue is full, the mapreduce is placed in this queue to be forwarded to another node.</li>
 * </ol>
 */
public interface Scheduler extends Remote {

  /**
   * Finds the {@code Responsible Node} for the specified job and queues the job on it.
   * If the local node is the {@code responsible} node, it accepts a job and queues it to be
   * delegated as tasks to {@code initial} nodes.
   */
  JobSchedulingResult addJob(final Job job) throws RemoteException;

  /**
   * Accepts a mapreduce as the {@code initial node} or as a {@code remote node}, queuing it for
   * execution or forwarding.
   */
  TaskSchedulingResult addTask(final Task task) throws RemoteException;

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
