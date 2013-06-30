package edu.asu.ying.mapreduce.net;

import edu.asu.ying.mapreduce.table.Table;
import edu.asu.ying.mapreduce.table.TableID;
import edu.asu.ying.mapreduce.task.scheduling.Scheduler;

/**
 * {@code Node} provides the interface common to local and remote nodes.
 */
public interface Node {

  Scheduler getScheduler();
  Table getTable(final TableID id);
}
