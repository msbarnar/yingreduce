package edu.asu.ying.wellington.mapreduce.task;

/**
 *
 */
public class TaskSchedulingException extends TaskException {

  public TaskSchedulingException(String message) {
    super(message);
  }

  public TaskSchedulingException(String message, Throwable cause) {
    super(message, cause);
  }
}
