package edu.asu.ying.mapreduce.task;

import edu.asu.ying.mapreduce.table.TableID;

/**
 * {@code Job} is the base interface for a full map/reduce job.
 */
public interface Job {

  JobID getJobId();
  TableID getSourceTableId();
}
