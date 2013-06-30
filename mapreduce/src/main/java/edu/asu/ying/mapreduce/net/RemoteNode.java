package edu.asu.ying.mapreduce.net;

import edu.asu.ying.mapreduce.task.scheduling.Scheduler;

/**
 * Provides an interface to a remote node and access to its resources.
 */
public interface RemoteNode {

  Scheduler getScheduler();
}
