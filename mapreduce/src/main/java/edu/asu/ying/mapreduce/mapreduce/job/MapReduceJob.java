package edu.asu.ying.mapreduce.mapreduce.job;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.database.table.TableID;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.p2p.RemotePeer;

/**
 *
 */
public final class MapReduceJob implements Job {

  private static final long SerialVersionUID = 1L;

  private final TaskID jobID;
  private final TableID tableID;
  private RemotePeer responsibleNode;
  private RemotePeer reducerNode;
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
  public void setResponsibleNode(final RemotePeer node) {
    this.responsibleNode = node;
  }

  @Override
  public final RemotePeer getResponsibleNode() {
    return this.responsibleNode;
  }

  @Override
  public void setReducerNode(final RemotePeer node) {
    this.reducerNode = node;
  }

  @Override
  public final RemotePeer getReducerNode() {
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
  public final void setStartTime(final RemotePeer referenceNode) {
    try {
      this.startTime = referenceNode.getTimeMs();
    } catch (final RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
      // Make every time calculation negative so we know it's untrustworthy
      this.startTime = Long.MAX_VALUE;
    }
  }

  @Override
  public final long getTimeElapsed() {
    return System.currentTimeMillis() - this.startTime;
  }
}
