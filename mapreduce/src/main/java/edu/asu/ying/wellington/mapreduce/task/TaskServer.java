package edu.asu.ying.wellington.mapreduce.task;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.job.scheduling.ForwardingQueueExecutor;
import edu.asu.ying.wellington.mapreduce.job.scheduling.JobDelegator;
import edu.asu.ying.wellington.mapreduce.job.scheduling.LocalQueueExecutor;
import edu.asu.ying.wellington.mapreduce.job.scheduling.RemoteQueueExecutor;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;

/**
 *
 */
public class TaskServer implements TaskService {

  private static final int MAX_QUEUE_SIZE = 1;

  private final LocalNode localNode;
  // The job delegator accepts unstarted jobs, splits them into tasks, and delegates each task to
  // its initial node.
  private final JobDelegator jobDelegator;
  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final QueueExecutor forwardingQueue;
  private final QueueExecutor localQueue = new LocalQueueExecutor();
  private final QueueExecutor remoteQueue = new RemoteQueueExecutor();


  public TaskServer(LocalNode localNode) {
    this.localNode = localNode;
    // Set up forwarding queue with node reference so it can find neighbors
    this.forwardingQueue = new ForwardingQueueExecutor(localNode,
                                                       (RemoteQueueExecutor) this.remoteQueue);
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
    System.out.println("Task: ".concat(task.getId().toString()));
  }

  private boolean isInitialNodeFor(Task task) {
    return true;
  }
}
