package edu.asu.ying.wellington.mapreduce.task;

import edu.asu.ying.wellington.mapreduce.Service;

/**
 *
 */
public interface TaskService extends Service {

  void accept(Task task) throws TaskException;
}
