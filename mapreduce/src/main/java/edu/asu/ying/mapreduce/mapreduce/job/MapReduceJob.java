package edu.asu.ying.mapreduce.mapreduce.job;

import edu.asu.ying.mapreduce.common.HasProperties;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.mapreduce.yingtable.TableID;
import edu.asu.ying.p2p.RemoteNode;

/**
 *
 */
public final class MapReduceJob implements Job {

  private static final long SerialVersionUID = 1L;

  private final TaskID jobID;
  private final TableID tableID;
  private RemoteNode responsibleNode;
  private RemoteNode reducerNode;
  private int numTasks;
  private long startTime;

  public MapReduceJob(final TableID tableID) {
    this.jobID = new TaskID();
    this.tableID = tableID;
  }

  @Override
  public final TaskID getID() {
    return this.jobID;
  }

  @Override
  public final TableID getTableID() {
    return this.tableID;
  }

  @Override
  public void setResponsibleNode(final RemoteNode node) {
    this.responsibleNode = node;
  }

  @Override
  public final RemoteNode getResponsibleNode() {
    return this.responsibleNode;
  }

  @Override
  public void setReducerNode(final RemoteNode node) {
    this.reducerNode = node;
  }

  @Override
  public final RemoteNode getReducerNode() {
    return this.reducerNode;
  }

  @Override
  public final void setNumTasks(final int n) {
    this.numTasks = n;
  }
  @Override
  public final int getNumTasks() {
    return this.numTasks;
  }

  @Override
  public final void setStartTime() {
    this.startTime = System.currentTimeMillis();
  }
  @Override
  public final long getTimeElapsed() {
    return System.currentTimeMillis() - this.startTime;
  }
}
