package edu.asu.ying.mapreduce.mapreduce.scheduling;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.mapreduce.execution.ForwardingTaskQueue;
import edu.asu.ying.mapreduce.mapreduce.execution.TaskQueue;
import edu.asu.ying.mapreduce.mapreduce.job.JobDelegator;
import edu.asu.ying.mapreduce.mapreduce.job.JobDelegatorImpl;
import edu.asu.ying.mapreduce.mapreduce.task.TaskSchedulingResult;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskHistory;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;

/**
 * The {@code SchedulerImpl} is responsible for accepting a {@link Task} from another
 * node (or from the local node, if the mapreduce was started locally) and queuing it for execution in
 * one of the following queues, deferring to {@code forwarding} if {@code local} is full.
 * <ol>
 *   <li>{@code Local} - mapreduce are executed directly on the local node.</li>
 *   <li>{@code Forwarding} - mapreduce are sent to either the local node's {@code remote} queue, or
 *   to the forwarding queue of a random immediately-connected node.</li>
 * </ol>
 * Once the scheduler has placed the mapreduce in a queue, the mapreduce is taken over by that queue's
 * {@link edu.asu.ying.mapreduce.mapreduce.execution.TaskQueue}.
 */
public class SchedulerImpl implements LocalScheduler {

  /**
   * Provides the implementation of {@code RemoteScheduler} which will be accessible to remote peers
   * when exported. The proxy implementation glues the remote scheduler proxy to the concrete local
   * scheduler implementation while implementing the appropriate patterns to be RMI-compatible.
   */
  private final class SchedulerProxyImpl implements RemoteScheduler {

    private final LocalScheduler localScheduler;

    private SchedulerProxyImpl(final LocalScheduler localScheduler) {
      this.localScheduler = localScheduler;
    }

    @Override
    public JobSchedulingResult startJob(Job job) throws RemoteException {
      return null;
    }

    @Override
    public TaskSchedulingResult delegateTask(Task task) throws RemoteException {
      return null;
    }
  }
  /***********************************************************************************************/

  // Glue between the RMI interface and the local implementation
  private final RemoteScheduler schedulerProxy;

  // The node on which this scheduler is running
  private final LocalNode localNode;

  // The job delegator accepts unstarted jobs, splits them into tasks, and delegates each task to
  // its initial node.
  private final JobDelegator jobDelegator;

  private static final int MAX_QUEUE_SIZE = 3;

  // Ql and Qr are bounded, but Qf is just a pipe
  private final BlockingQueue<Task> localQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
  private final TaskQueue forwardingQueue;
  private final BlockingQueue<Task> remoteQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

  // The executors watch the mapreduce queues and execute
  //private final TaskQueue localExecutor = new LocalTaskQueueExecutor(this.localQueue);
 // private final TaskQueue remoteExecutor = new RemoteTaskQueueExecutor(this.remoteQueue);

  public SchedulerImpl(final LocalNode localNode) {

    this.localNode = localNode;

    //this.localExecutor.start();

    this.forwardingQueue = new ForwardingTaskQueue(this, localNode);
    this.forwardingQueue.start();

   // this.remoteExecutor.start();

    // Start the worker that splits jobs into tasks and sends each to its initial node
    this.jobDelegator = new JobDelegatorImpl(this.localNode);
    this.jobDelegator.start();
  }

  public final int getBackpressure() throws RemoteException {
    return this.remoteQueue.size() + this.forwardingQueue.size();
  }

  /**
   * Finds the {@code Responsible Node} for the specified job and queues the job on it.
   * If the local node is the {@code responsible} node, it accepts a job and queues it to be
   * delegated as tasks to {@code initial} nodes.
   */
  @Override
  public final JobSchedulingResult createJob(final Job job) {
    // TODO: Find the responsible node by finding the node with the first page of the table
    // FIXME: pick a random node
    final List<RemoteNode> neighbors = this.localNode.getNeighbors();
    final int rnd = (new Random()).nextInt(neighbors.size());

    final RemoteNode node = neighbors.get(rnd);
    try {
      return node.getScheduler().delegateJob(job);
    } catch (final RemoteException e) {
      return new JobSchedulingResult(job, this.localNode.getRemoteReference(), e);
    }
  }

  @Override
  public JobSchedulingResult acceptJob(final Job job) throws RemoteException {
    this.jobQueue.add(job);
    return new JobSchedulingResult(job, this.localUri)
        .setResult(JobSchedulingResult.Result.Scheduled);
  }
  /**
   * Accepts a {@link Task} and appends it to the appropriate queue:
   * <ol>
   *   <li>{@code Local} - if the current node is the {@code initial} node, and the queue is not
   *   full.</li>
   *   <li>{@code Forwarding} - if the current node is <b>not</b> the initial node, the mapreduce is
   *   placed in this queue. The mapreduce will then either be placed in the {@code Remote} queue
   *   or, if it is full, in a child node's {@code Forwarding} queue.</li>
   * </ol>
   */
  @Override
  public TaskSchedulingResult acceptTask(final Task task) throws RemoteException {

    task.touch(this.localNode.getProxy());

    final TaskSchedulingResult result = new TaskSchedulingResult();

    final TaskHistory.Entry historyEntry = task.getHistory().last();

    // If this is the initial node, try to execute the mapreduce locally.
    if (task.isCurrentlyAtInitialNode()) {
      historyEntry.setNodeRole(TaskHistory.NodeRole.Initial);
      // Add the mapreduce if the local queue is not full
      result.setTaskScheduled(this.queueLocally(task, historyEntry));
      // If the local queue was full, forward the mapreduce
      if (!result.isTaskScheduled()) {
        result.setTaskScheduled(this.queueForward(task, historyEntry));
      }
    } else {
      // If this is not the initial node, put the mapreduce straight in the forwarding queue
      result.setTaskScheduled(this.queueForward(task, historyEntry));
    }

    System.out.println(String.format("[%s] Task: %s", this.localNode.getIdentifier(),
                                     historyEntry.getSchedulerAction()));

    return result;
  }

  /**
   * Attempts to queue the {@link Task} in the local queue for execution on the
   * {@code initial node}.
   * @param task the mapreduce to be queued.
   * @param historyEntry details regarding the queuing of the mapreduce will be indicated in this entry.
   * @return {@code true} if the mapreduce was placed on the queue; {@code false} if the queue was full.
   */
  private boolean queueLocally(final Task task, final TaskHistory.Entry historyEntry) {
    final boolean isQueued = this.localQueue.offer(task);
    if (isQueued) {
      // Mark the mapreduce as being performed on the initial node
      historyEntry.setSchedulerAction(TaskHistory.SchedulerAction.QueuedLocally);
      task.getHistory().append(historyEntry);
    }
    return isQueued;
  }

  /**
   * Places the {@link Task} in the forwarding queue to be executed remotely.
   * @param task the mapreduce to be queued.
   * @param historyEntry details regarding the queuing of the mapreduce will be indicated in this entry.
   * @return {@code true}.
   */
  private boolean queueForward(final Task task, final TaskHistory.Entry historyEntry) {
    historyEntry.setSchedulerAction(TaskHistory.SchedulerAction.Forwarded);
    task.getHistory().append(historyEntry);
    return this.for.add(task);
  }

  public final TaskQueue getRemoteQueue() {
    return this.remoteQueue;
  }

  public final RemoteScheduler getProxy() {
    return this.schedulerProxy;
  }

  private TaskHistory.Entry createHistoryEntry() {
    return new TaskHistory.Entry(this.localNode.getProxy());
  }
}
