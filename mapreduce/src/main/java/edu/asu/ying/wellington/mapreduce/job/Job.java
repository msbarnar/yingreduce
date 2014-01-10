package edu.asu.ying.wellington.mapreduce.job;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import edu.asu.ying.wellington.RemoteNode;

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

  private static final long serialVersionUID = 1L;

  private final String name;
  private Status status;

  private final String tableName;

  private RemoteNode responsibleNode;
  private Set<RemoteNode> reducerNodes;
  private int reducerCount = DEFAULT_REDUCER_COUNT;

  private int numTasks;
  private long startTime;

  // Keep track of what has been done with the job
  private final JobHistory history = new JobHistory();

  public Job(String tableName) {
    this.name = UUID.randomUUID().toString();
    this.tableName = tableName;
    this.status = Status.Created;
  }

  public JobHistory getHistory() {
    return this.history;
  }

  public String getName() {
    return name;
  }

  public String getTableName() {
    return tableName;
  }

  public void setResponsibleNode(RemoteNode node) {
    this.responsibleNode = node;
  }

  public RemoteNode getResponsibleNode() {
    return this.responsibleNode;
  }

  public void setReducerNodes(Set<RemoteNode> reducers) {
    this.reducerNodes = ImmutableSet.copyOf(reducers);
    this.reducerCount = this.reducerNodes.size();
  }

  public Set<RemoteNode> getReducerNodeIDs() {
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
