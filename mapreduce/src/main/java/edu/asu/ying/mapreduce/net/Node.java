package edu.asu.ying.mapreduce.net;

import edu.asu.ying.mapreduce.task.scheduling.Scheduler;

/**
 * {@code Node} provides the interface common to local and remote nodes.
 */
public interface Node {

  Scheduler getScheduler();

  NodeURI getNodeURI();
}
