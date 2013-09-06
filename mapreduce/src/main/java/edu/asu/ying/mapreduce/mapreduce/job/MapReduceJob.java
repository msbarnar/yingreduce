package edu.asu.ying.mapreduce.mapreduce.job;

import edu.asu.ying.mapreduce.common.HasProperties;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.mapreduce.yingtable.TableID;
import edu.asu.ying.p2p.RemoteNode;

/**
 *
 */
public class MapReduceJob implements Job {

  private static final long SerialVersionUID = 1L;

  private final TaskID jobID;
  private final TableID tableID;
  private RemoteNode responsibleNode;
  private RemoteNode reducerNode;

  public MapReduceJob(final TableID tableID) {
    this.jobID = new TaskID(tableID.toString().concat("0"));
    this.tableID = tableID;
  }

  @Override
  public TaskID getID() {
    return this.jobID;
  }

  @Override
  public TableID getTableID() {
    return this.tableID;
  }

  @Override
  public void setResponsibleNode(final RemoteNode node) {
    this.responsibleNode = node;
  }

  @Override
  public RemoteNode getResponsibleNode() {
    return this.responsibleNode;
  }

  @Override
  public void setReducerNode(final RemoteNode node) {
    this.reducerNode = node;
  }

  @Override
  public RemoteNode getReducerNode() {
    return this.reducerNode;
  }
}
