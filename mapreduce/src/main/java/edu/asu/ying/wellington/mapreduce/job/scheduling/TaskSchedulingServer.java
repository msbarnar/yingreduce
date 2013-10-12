package edu.asu.ying.wellington.mapreduce.job.scheduling;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 * The {@code TaskSchedulingServer} is responsible for accepting a {@link
 * edu.asu.ying.wellington.mapreduce.task.Task} from another node (or from the local node, if the
 * mapreduce was started locally) and queuing it for execution in one of the following queues,
 * deferring to {@code forwarding} if {@code local} is full. <ol> <li>{@code Local} - mapreduce are
 * executed directly on the local node.</li> <li>{@code Forwarding} - mapreduce are sent to either
 * the local node's {@code remote} queue, or to the forwarding queue of a random
 * immediately-connected node.</li> </ol> Once the scheduler has placed the mapreduce in a queue,
 * the mapreduce is taken over by that queue's {@link edu.asu.ying.common.concurrency.QueueExecutor}.
 */
public final class TaskSchedulingServer implements TaskService {

  private static final int MAX_QUEUE_SIZE = 1;

  private final LocalNode localNode;
  // The job delegator accepts unstarted jobs, splits them into tasks, and delegates each task to
  // its initial node.
  private final JobDelegator jobDelegator;
  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final QueueExecutor forwardingQueue;
  private final QueueExecutor localQueue = new LocalQueueExecutor();
  private final QueueExecutor remoteQueue = new RemoteQueueExecutor();


  public TaskSchedulingServer(LocalNode localNode) {
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
  }
}
