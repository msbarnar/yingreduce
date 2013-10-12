package edu.asu.ying.wellington.mapreduce.task;

/**
 *
 */
public interface TaskService {

  void accept(Task task) throws TaskException;
}
