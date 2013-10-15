package edu.asu.ying.wellington.mapreduce.job;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 *
 */
public final class Job implements Serializable {

  public enum Status {
    Created,
    Accepted,
    Rejected,
    Delegated
  }

  private static final int DEFAULT_REDUCER_COUNT = 3;
  private static final int MAX_REDUCER_COUNT = 128;

  private static final long SerialVersionUID = 1L;

  private final JobIdentifier jobID;
  private Status status;

  private final TableIdentifier tableID;

  private RemoteNode responsibleNode;
  private List<RemoteNode> reducerNodes;
  private int reducerCount = DEFAULT_REDUCER_COUNT;

  private int numTasks;
  private long startTime;

  // Keep track of what has been done with the job
  private final JobHistory history = new JobHistory();

  public Job(TableIdentifier tableID) {
    this.jobID = JobIdentifier.random();
    this.tableID = tableID;
    this.status = Status.Created;
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

  public void setResponsibleNode(RemoteNode nodeID) {
    this.responsibleNode = nodeID;
  }

  public RemoteNode getResponsibleNode() {
    return this.responsibleNode;
  }

  public void setReducerNodes(Collection<RemoteNode> reducers) {
    this.reducerNodes = ImmutableList.copyOf(reducers);
    this.reducerCount = this.reducerNodes.size();
  }

  public List<RemoteNode> getReducerNodeIDs() {
    return this.reducerNodes;
  }

  public void setReducerCount(int count) {
    this.reducerCount = Math.min(MAX_REDUCER_COUNT, Math.max(1, count));
  }

  public int getReducerCount() {
    return this.reducerCount;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Status getStatus() {
    return this.status;
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
