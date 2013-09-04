package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.Serializable;
import java.net.InetAddress;

import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;

/**
 * A {@code Task} is the basic unit of work in the map/reduce system.
 * </p>
 * Tasks are how nodes communicate pending or completed work.
 */
public interface Task extends Serializable {

  Properties getProperties();

  Job getParentJob();

  TaskID getId();

  /**
   * Returns a {@link java.rmi.Remote} proxy to the node which owns the data to which this task
   * applies.
   */
  RemoteNode getInitialNode();

  /**
   * Returns the parameters used to manage the starting of this task on its final executing node.
   */
  TaskStartParameters getTaskStartParameters();

  /**
   * Returns a history of all of the schedulers that have handled this task.
   */
  TaskHistory getHistory();

  Serializable run();

  /**
   * Returns {@code true} if this is the initial node (the node carrying the table) for the
   * specified mapreduce.
   */
  boolean isCurrentlyAtInitialNode();
}
