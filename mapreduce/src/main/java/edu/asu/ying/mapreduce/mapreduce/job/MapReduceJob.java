package edu.asu.ying.mapreduce.mapreduce.job;

import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.mapreduce.yingtable.TableID;

/**
 *
 */
public class MapReduceJob implements Job {

  private static final long SerialVersionUID = 1L;

  private final TaskID id = new TaskID();
  private final TableID table;

  public MapReduceJob(final TableID table) {
    this.table = table;
  }

  @Override
  public TaskID getId() {
    return this.id;
  }

  @Override
  public TableID getSourceTableId() {
    return this.table;
  }
}
