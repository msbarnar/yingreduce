package edu.asu.ying.wellington.mapreduce;

import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;

/**
 *
 */
public interface TaskService {

  void accept(Task task) throws TaskException;
}
