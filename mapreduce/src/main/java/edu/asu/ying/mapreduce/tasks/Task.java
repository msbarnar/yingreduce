package edu.asu.ying.mapreduce.tasks;

import java.lang.annotation.IncompleteAnnotationException;

import edu.asu.ying.mapreduce.common.Properties;

/**
 * A {@code Task} is the basic unit of work in the map/reduce system.
 * </p>
 * Tasks are how nodes communicate pending or completed work.
 */
public interface Task {

  Properties getProperties();

  String getId();
  TaskStartParameters getTaskStartParameters();

  /**
   * Checks that a task has all of the necessary information before attempting to schedule it.
   */
  void validate() throws InvalidTaskException;
}
