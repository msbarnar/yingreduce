package edu.asu.ying.mapreduce.task;

/**
 *
 */
public class TaskException extends Exception {

  public TaskException(String message) {
    super(message);
  }

  public TaskException(String message, Throwable cause) {
    super(message, cause);
  }
}
