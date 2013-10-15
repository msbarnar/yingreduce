package edu.asu.ying.wellington.mapreduce.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.dfs.page.PageIdentifier;
import edu.asu.ying.wellington.dfs.table.TableNotFoundException;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;

/**
 *
 */
@Singleton
public class TaskScheduler implements TaskService {

  private final LocalNode localNode;
  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final QueueExecutor<Task> forwardingQueue;
  private final QueueExecutor<Task> localQueue;
  private final QueueExecutor<Task> remoteQueue;


  @Inject
  private TaskScheduler(LocalNode localNode) {
    this.localNode = localNode;
    // Set up queues for task execution/forwarding
    this.localQueue = new LocalQueueExecutor(localNode);
    this.remoteQueue = new RemoteQueueExecutor(localNode);
    this.forwardingQueue = new ForwardingQueueExecutor(localNode, remoteQueue);
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
      return localNode.getDFSService().getTable(pageId.getTableID()).hasPage(pageId.getIndex());
    } catch (TableNotFoundException e) {
      return false;
    }
  }
}
