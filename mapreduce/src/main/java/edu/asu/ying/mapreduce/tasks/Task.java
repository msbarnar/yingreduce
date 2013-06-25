package edu.asu.ying.mapreduce.tasks;

import java.io.Serializable;
import java.lang.annotation.IncompleteAnnotationException;

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
   * Checks that a task has all of the necessary information before attempting to schedule it.
   */
  void validate() throws InvalidTaskException;

  /**
   * Returns a history of all of the schedulers that have observed this task.
   */
  TaskHistory getHistory();
}
