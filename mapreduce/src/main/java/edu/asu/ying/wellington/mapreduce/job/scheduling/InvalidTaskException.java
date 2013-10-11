package edu.asu.ying.wellington.mapreduce.job.scheduling;

/**
 *
 */
public class InvalidTaskException extends Exception {

  private final Task task;

  public InvalidTaskException(final Task task) {
    this.task = task;
  }

  public InvalidTaskException(final Task task, final String detail) {
    super(detail);
    this.task = task;
  }

  public final Task getTask() {
    return this.task;
  }
}
