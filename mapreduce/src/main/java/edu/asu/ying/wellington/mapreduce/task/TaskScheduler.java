package edu.asu.ying.wellington.mapreduce.task;

import com.google.inject.Inject;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.TableNotFoundException;

/**
 *
 */
public class TaskScheduler implements TaskService {

  private final DFSService dfsService;

  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final QueueExecutor<Task> forwardingQueue;
  private final QueueExecutor<Task> localQueue;
  private final QueueExecutor<Task> remoteQueue;


  @Inject
  private TaskScheduler(DFSService dfsService,
                        ForwardingQueueExecutor forwardingQueue,
                        LocalQueueExecutor localQueue,
                        RemoteQueueExecutor remoteQueue) {

    this.dfsService = dfsService;

    this.localQueue = localQueue;
    this.remoteQueue = remoteQueue;
    this.forwardingQueue = forwardingQueue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
    // Start everything explicitly so we don't start any threads in constructors
    localQueue.start();
    remoteQueue.start();
    forwardingQueue.start();
  }

  @Override
  public void accept(Task task) throws TaskException {
    // Initial tasks go in the local queue first if available, else everything gets forwarded
    if (isInitialNodeFor(task)) {
      queueLocal(task);
    } else {
      queueForward(task);
    }
  }

  private void queueLocal(Task task) throws TaskSchedulingException {
    // Forward to the shortest of {Ql, Qf}
    if (localQueue.size() <= forwardingQueue.size()) {
      // If the local queue won't take it, forward it
      if (!localQueue.offer(task)) {
        queueForward(task);
      }
    } else {
      queueForward(task);
    }
  }

  private void queueForward(Task task) throws TaskSchedulingException {
    if (!forwardingQueue.offer(task)) {
      throw new TaskSchedulingException("Forwarding queue refused task; no recourse available.");
    }
  }

  /**
   * Returns true if the local DFS service has the correct page of the table specified by the task.
   */
  private boolean isInitialNodeFor(Task task) {
    PageIdentifier pageId = task.getTargetPageID();
    try {
      return dfsService.getTable(pageId.getTableID()).hasPage(pageId.getIndex());
    } catch (TableNotFoundException e) {
      return false;
    }
  }
}
