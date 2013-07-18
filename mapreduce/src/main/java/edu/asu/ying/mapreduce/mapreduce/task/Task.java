package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.Serializable;
import java.net.InetAddress;

import edu.asu.ying.mapreduce.common.Properties;

/**
 * A {@code Task} is the basic unit of work in the map/reduce system.
 * </p>
 * Tasks are how nodes communicate pending or completed work.
 */
public interface Task extends Serializable {

  Properties getProperties();

  TaskID getId();
  TaskStartParameters getTaskStartParameters();

  /**
   * Checks that a mapreduce has all of the necessary information before attempting to addTask it.
   */
  void validate() throws InvalidTaskException;

  /**
   * Returns a history of all of the schedulers that have observed this mapreduce.
   */
  TaskHistory getHistory();

  /**
   * Returns the IP address of the node responsible for managing the mapreduce (the owner of the first
   * mapreduce segment).
   */
  InetAddress getResponsibleNodeAddress();

  /**
   * Returns {@code true} if this is the initial node (the node carrying the table) for the specified
   * mapreduce.
   * </p>
   * This is the initial node if the most recent node to handle the mapreduce was the
   * {@code responsible node} for the job.
   */
  boolean isCurrentlyAtInitialNode();
}
