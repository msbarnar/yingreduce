package edu.asu.ying.mapreduce.mapreduce.job;

import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.mapreduce.table.TableID;

/**
 * {@code Job} is the base interface for a full map/reduce job.
 */
public interface Job {

  TaskID getJobId();
  TableID getSourceTableId();
}
