package edu.asu.ying.mapreduce.task;

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

  String getId();
  TaskStartParameters getTaskStartParameters();

  /**
   * Checks that a task has all of the necessary information before attempting to addTask it.
   */
  void validate() throws InvalidTaskException;

  /**
   * Returns a history of all of the schedulers that have observed this task.
   */
  TaskHistory getHistory();

  /**
   * Returns the IP address of the node responsible for managing the task (the owner of the first
   * task segment).
   */
  InetAddress getResponsibleNodeAddress();

  /**
   * Returns {@code true} if this is the initial node (the node carrying the table) for the specified
   * task.
   * </p>
   * This is the initial node if the most recent node to handle the task was the
   * {@code responsible node} for the job.
   */
  boolean isCurrentlyAtInitialNode();
}
