package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public final class LocalQueueExecutor extends QueueExecutor<Task> {

  private static final Logger log = Logger.getLogger(LocalQueueExecutor.class);

  private final DFSService dfs;

  private final TaskExecutor executor;

  @Inject
  private LocalQueueExecutor(DFSService dfs, TaskExecutor executor) {
    this.dfs = dfs;
    this.executor = executor;
  }

  @Override
  protected void process(Task task) {
    executor.execute(task);
  }
}
