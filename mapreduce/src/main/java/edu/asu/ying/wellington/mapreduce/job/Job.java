package edu.asu.ying.wellington.mapreduce.job;

import java.io.Serializable;

import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.mapreduce.net.NodeIdentifier;

/**
 *
 */
public final class Job implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final JobIdentifier jobID;
  private final TableIdentifier tableID;
  private NodeIdentifier responsibleNodeID;
  private int numTasks;
  private long startTime;

  // Keep track of what has been done with the job
  private final JobHistory history = new JobHistory();

  public Job(TableIdentifier tableID) {
    this.jobID = JobIdentifier.random();
    this.tableID = tableID;
  }

  public JobHistory getHistory() {
    return this.history;
  }

  public JobIdentifier getID() {
    return this.jobID;
  }

  public TableIdentifier getTableID() {
    return this.tableID;
  }

  public void setResponsibleNode(NodeIdentifier nodeID) {
    this.responsibleNodeID = nodeID;
  }

  public NodeIdentifier getResponsibleNodeID() {
    return this.responsibleNodeID;
  }

  public int getNumTasks() {
    return this.numTasks;
  }

  public void setNumTasks(int n) {
    this.numTasks = n;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getTimeElapsed() {
    return System.currentTimeMillis() - this.startTime;
  }
}
