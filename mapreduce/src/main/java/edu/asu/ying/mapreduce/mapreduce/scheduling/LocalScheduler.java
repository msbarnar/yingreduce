package edu.asu.ying.mapreduce.mapreduce.scheduling;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.execution.TaskQueue;
import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskSchedulingResult;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.p2p.NodeIdentifier;

/**
 * {@code LocalScheduler} is responsible for the allocation of {@code map} and {@code reduce} mapreduce.
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
public interface LocalScheduler {

  /**
   * Finds the {@code Responsible Node} for the specified job and queues the job on it.
   * If the local node is the {@code responsible} node, it accepts a job and queues it to be
   * delegated as tasks to {@code initial} nodes.
   */
  JobSchedulingResult createJob(final Job job);

  /**
   * Provides a {@link Remote} proxy to this scheduler. The proxy is <b>not exported</b> by default
   * and is therefore unavailable to RMI clients; the caller is responsible for exporting the proxy.
   */
  RemoteScheduler getProxy();

  LocalNode getLocalNode();

  TaskQueue getRemoteQueue();
}