package edu.asu.ying.mapreduce.mapreduce.job;

import java.io.Serializable;

import edu.asu.ying.mapreduce.database.table.TableID;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.p2p.RemotePeer;

/**
 * {@code Job} is the base interface for a full map/reduce job.
 */
public interface Job extends Serializable {

  TaskID getID();

  TableID getTableID();

  void setResponsibleNode(final RemotePeer node);

  RemotePeer getResponsibleNode();

  void setReducerNode(final RemotePeer node);

  RemotePeer getReducerNode();

  void setNumTasks(final int n);

  int getNumTasks();

  void setStartTime(final RemotePeer referenceNode);

  long getTimeElapsed();
}
