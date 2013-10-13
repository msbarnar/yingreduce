package edu.asu.ying.wellington.mapreduce.task;

import java.io.Serializable;

import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.net.NodeIdentifier;
import edu.asu.ying.wellington.mapreduce.net.RemoteNode;

/**
 * {@code Task} is the base class of all distributable mapreduce. </p> Properties defined by this
 * class are: <ul> <il>{@code mapreduce.id} - the universally unique ID of the mapreduce</il> </ul>
 */
public abstract class Task implements Serializable {

  private static final long SerialVersionUID = 1L;

  protected final TaskIdentifier taskID;
  protected final Job parentJob;

  protected RemoteNode initialNode;
  protected NodeIdentifier initialNodeID;

  protected Task(Job parentJob, TaskIdentifier taskID) {
    this.parentJob = parentJob;
    this.taskID = taskID;
    // Set the initial node ID as the node with the first page of the target table
    this.initialNodeID = NodeIdentifier.forString(
        this.parentJob.getTableID().forPage(0).toString());
  }

  public TaskIdentifier getId() {
    return this.taskID;
  }

  public Job getParentJob() {
    return this.parentJob;
  }

  public RemoteNode getInitialNode() {
    return this.initialNode;
  }

  public void setInitialNode(RemoteNode initialNode) {
    this.initialNode = initialNode;
  }

  public NodeIdentifier getInitialNodeID() {
    return this.initialNodeID;
  }

  public abstract Serializable run();
}
