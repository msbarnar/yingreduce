package edu.asu.ying.mapreduce.tasks;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * {@code ReducerReference} provides abstraction between a {@link edu.asu.ying.mapreduce.tasks.map.MapTask}
 * and the node responsible for reducing its results.
 */
public interface ReducerReference extends Serializable {

  InetAddress getNodeAddress();
}
