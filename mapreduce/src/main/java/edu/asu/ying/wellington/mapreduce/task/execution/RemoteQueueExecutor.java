package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
// FIXME: Abstract execution model away from queues; use unified page retrieval
public final class RemoteQueueExecutor extends QueueExecutor<Task> {

  private static final Logger log = Logger.getLogger(RemoteQueueExecutor.class);

  private final DFSService dfs;
  private final TaskExecutor executor;

  @Inject
  private RemoteQueueExecutor(DFSService dfs, TaskExecutor executor) {
    this.dfs = dfs;
    this.executor = executor;
  }

  @Override
  protected void process(Task task) {
    executor.execute(task);
  }
}
