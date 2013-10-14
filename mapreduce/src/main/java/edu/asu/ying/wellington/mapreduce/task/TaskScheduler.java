package edu.asu.ying.wellington.mapreduce.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.dfs.table.TableNotFoundException;
import edu.asu.ying.wellington.mapreduce.job.JobDelegator;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;

/**
 *
 */
@Singleton
public class TaskScheduler implements TaskService {

  private static final int MAX_QUEUE_SIZE = 1;

  private final LocalNode localNode;
  // The job delegator accepts unstarted jobs, splits them into tasks, and delegates each task to
  // its initial node.
  private final JobDelegator jobDelegator;
  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final QueueExecutor<Task> forwardingQueue;
  private final QueueExecutor<Task> localQueue;
  private final QueueExecutor<Task> remoteQueue;


  @Inject
  private TaskScheduler(LocalNode localNode) {
    this.localNode = localNode;
    // Set up queues for task execution/forwarding
    this.localQueue = new RemoteQueueExecutor(this.localNode);
    this.remoteQueue = new LocalQueueExecutor(this.localNode);
    this.forwardingQueue = new ForwardingQueueExecutor(localNode, this.remoteQueue);
    // Set up the delegator that splits jobs into tasks and sends them to initial nodes
    this.jobDelegator = new JobDelegator(this.localNode);

  }

  /**
   * {@inheritDoc}
   */
  public void start() {
    // Start everything explicitly so we don't start any threads in constructors
    this.localQueue.start();
    this.remoteQueue.start();
    this.forwardingQueue.start();
    this.jobDelegator.start();
  }

  @Override
  public void accept(Task task) throws TaskException {
    // Initial tasks go in the local queue first if available, else everything gets forwarded
    if (this.isInitialNodeFor(task)) {
      this.queueLocal(task);
    } else {
      if (!this.forwardingQueue.offer(task)) {
        throw new TaskSchedulingException("Forwarding queue refused task; no recourse available.");
      }
    }
  }

  private void queueLocal(Task task) throws TaskSchedulingException {
    // Forward to the shortest of {Ql, Qf}
    if (this.localQueue.size() < this.forwardingQueue.size()) {
      // If the local queue won't take it, forward it
      if (!this.localQueue.offer(task)) {
        this.forwardingQueue.offer(task);
      }
    } else {
      if (!this.forwardingQueue.offer(task)) {
        throw new TaskSchedulingException("Forwarding queue refused task; no recourse available.");
      }
    }
  }

  /**
   * Returns true if the local DFS service has the correct page of the table specified by the task.
   */
  private boolean isInitialNodeFor(Task task) {
    TableIdentifier taskTableID = task.getTableID();
    try {
      return this.localNode.getDFSService()
          .getTable(taskTableID)
          .hasPage(taskTableID.getPageIndex());
    } catch (TableNotFoundException e) {
      return false;
    }
  }
}
