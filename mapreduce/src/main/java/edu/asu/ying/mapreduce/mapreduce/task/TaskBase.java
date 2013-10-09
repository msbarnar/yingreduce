package edu.asu.ying.mapreduce.mapreduce.task;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.p2p.RemotePeer;

/**
 * {@code TaskBase} is the base class of all distributable mapreduce. </p> Properties defined by
 * this class are: <ul> <il>{@code mapreduce.id} - the universally unique ID of the mapreduce</il>
 * </ul>
 */
public abstract class TaskBase implements Task {

  private static final long SerialVersionUID = 1L;

  protected final TaskID taskID;
  protected final TaskHistory history;
  protected final Job parentJob;

  protected TaskStartParameters startParameters;
  protected RemotePeer initialNode;

  protected TaskBase(final Job parentJob, final TaskID taskID) {
    this.history = new TaskHistory();
    this.parentJob = parentJob;
    this.taskID = taskID;
  }

  public final TaskID getId() {
    return this.taskID;
  }

  public final Job getParentJob() {
    return this.parentJob;
  }

  /**
   * The {@code TaskStartParameters} define the timing of the mapreduce's starting.
   *
   * @return the mapreduce's start parameters, or {@link TaskStartParameters#Default} if they are
   *         not set.
   */
  public final TaskStartParameters getTaskStartParameters() {
    return this.startParameters;
  }

  /**
   * The mapreduce's history is a log of the schedulers that have visited the mapreduce and the
   * actions they have performed.
   */
  public final TaskHistory getHistory() {
    return this.history;
  }

  public final RemotePeer getInitialNode() {
    return this.initialNode;
  }

  public final void setInitialNode(final RemotePeer initialNode) {
    this.initialNode = initialNode;
  }
}
