package edu.asu.ying.wellington.mapreduce.job.scheduling;

import java.io.Serializable;

import edu.asu.ying.p2p.RemotePeer;

/**
 * A {@code Task} is the basic unit of work in the map/reduce system. </p> Tasks are how nodes
 * communicate pending or completed work.
 */
public interface Task extends Serializable {

  Job getParentJob();

  TaskID getId();

  /**
   * Returns a {@link java.rmi.Remote} proxy to the node which owns the data to which this task
   * applies.
   */
  RemotePeer getInitialNode();

  void setInitialNode(final RemotePeer initialNode);

  /**
   * Returns the parameters used to manage the starting of this task on its final executing node.
   */
  TaskStartParameters getTaskStartParameters();

  Serializable run();
}
