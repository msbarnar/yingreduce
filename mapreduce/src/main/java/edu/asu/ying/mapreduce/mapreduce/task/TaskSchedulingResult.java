package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.Serializable;

/**
 *
 */
public class TaskSchedulingResult implements Serializable {

  private static final long SerialVersionUID = 1L;

  // Indicates that the node accepted the mapreduce into one of its queues.
  private boolean taskScheduled;

  public TaskSchedulingResult() {
  }

  public TaskSchedulingResult(final boolean taskScheduled) {
    this.taskScheduled = taskScheduled;
  }

  public boolean isTaskScheduled() {
    return taskScheduled;
  }

  public void setTaskScheduled(boolean taskScheduled) {
    this.taskScheduled = taskScheduled;
  }
}
