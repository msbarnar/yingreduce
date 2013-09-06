package edu.asu.ying.mapreduce.mapreduce.job;

import java.io.Serializable;
import java.net.InetAddress;

import edu.asu.ying.mapreduce.common.HasProperties;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.mapreduce.yingtable.TableID;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;

/**
 * {@code Job} is the base interface for a full map/reduce job.
 */
public interface Job extends Serializable {

  TaskID getID();
  TableID getTableID();

  void setResponsibleNode(final RemoteNode node);
  RemoteNode getResponsibleNode();

  void setReducerNode(final RemoteNode node);
  RemoteNode getReducerNode();

  void setNumTasks(final int n);
  int getNumTasks();

  void setStartTime();
  long getTimeElapsed();
}
