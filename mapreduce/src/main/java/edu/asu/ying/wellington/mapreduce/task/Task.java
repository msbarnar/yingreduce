package edu.asu.ying.wellington.mapreduce.task;

import java.io.Serializable;

import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.mapreduce.job.Job;

/**
 * {@code Task} is the base class of all distributable mapreduce. </p> Properties defined by this
 * class are: <ul> <il>{@code mapreduce.id} - the universally unique ID of the mapreduce</il> </ul>
 */
public abstract class Task implements Serializable {

  private static final long SerialVersionUID = 1L;

  protected final TaskIdentifier taskID;
  protected final Job parentJob;
  protected final PageName targetPage;

  protected RemoteNode initialNode;

  protected Task(Job parentJob, TaskIdentifier taskID, PageName targetPage) {
    this.parentJob = parentJob;
    this.taskID = taskID;
    this.targetPage = targetPage;
  }

  public TaskIdentifier getId() {
    return taskID;
  }

  public Job getParentJob() {
    return parentJob;
  }

  public RemoteNode getInitialNode() {
    return initialNode;
  }

  public void setInitialNode(RemoteNode initialNode) {
    this.initialNode = initialNode;
  }

  public PageName getTargetPageID() {
    return targetPage;
  }

  public abstract Serializable run();
}
