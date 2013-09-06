package edu.asu.ying.mapreduce.mapreduce.scheduling;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import edu.asu.ying.mapreduce.mapreduce.queuing.ForwardingTaskQueue;
import edu.asu.ying.mapreduce.mapreduce.queuing.LocalTaskQueue;
import edu.asu.ying.mapreduce.mapreduce.queuing.RemoteTaskQueue;
import edu.asu.ying.mapreduce.mapreduce.queuing.TaskQueue;
import edu.asu.ying.mapreduce.mapreduce.job.JobDelegator;
import edu.asu.ying.mapreduce.mapreduce.job.JobDelegatorImpl;
import edu.asu.ying.mapreduce.mapreduce.task.TaskCompletion;
import edu.asu.ying.mapreduce.mapreduce.task.TaskSchedulingResult;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskHistory;
import edu.asu.ying.p2p.RemoteNode;
import edu.asu.ying.p2p.rmi.RMIActivator;

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
 * {@link edu.asu.ying.mapreduce.mapreduce.queuing.TaskQueue}.
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
    public JobSchedulingResult acceptJobAsResponsibleNode(final Job job) throws RemoteException {
      return this.localScheduler.acceptJobAsResponsibleNode(job);
    }

    @Override
    public TaskSchedulingResult acceptTaskAsInitialNode(final Task task) throws RemoteException {
      return this.localScheduler.acceptTaskAsInitialNode(task);
    }

    @Override
    public final void reduceTaskCompletion(final TaskCompletion completion) throws RemoteException {
      this.localScheduler.reduceTaskCompletion(completion);
    }

    @Override
    public final int getBackpressure() throws RemoteException {
      return this.localScheduler.getRemoteQueue().size();
    }

    @Override
    public final RemoteNode getNode() throws RemoteException {
      return this.localScheduler.getLocalNode().getProxy();
    }
  }
  /***********************************************************************************************/

  // Glue between the RMI interface and the local implementation
  private RemoteScheduler schedulerProxy;
  // Necessary to keep alive the proxy implementation
  private final RemoteScheduler schedulerProxyTarget;

  // The node on which this scheduler is running
  private final LocalNode localNode;

  // The job delegator accepts unstarted jobs, splits them into tasks, and delegates each task to
  // its initial node.
  private final JobDelegator jobDelegator;

  private static final int MAX_QUEUE_SIZE = 3;

  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final TaskQueue forwardingQueue;
  private final TaskQueue localQueue = new LocalTaskQueue(MAX_QUEUE_SIZE, this);
  private final TaskQueue remoteQueue = new RemoteTaskQueue(MAX_QUEUE_SIZE, this);

  // The executors watch the mapreduce queues and execute
  //private final TaskQueue localExecutor = new LocalTaskQueueExecutor(this.localQueue);
 // private final TaskQueue remoteExecutor = new RemoteTaskQueueExecutor(this.remoteQueue);

  public SchedulerImpl(final LocalNode localNode) {

    this.localNode = localNode;

    // Initialize the implementation for the glue proxy
    this.schedulerProxyTarget = new SchedulerProxyImpl(this);

    // Set up forwarding queue with node reference so it can find neighbors
    this.forwardingQueue = new ForwardingTaskQueue(this, this.localNode);

    // Set up the delegator that splits jobs into tasks and sends them to initial nodes
    this.jobDelegator = new JobDelegatorImpl(this.localNode);
  }

  @Override
  public final void export(final RMIActivator activator) {
    // Create the proxy which will glue the remote interface to the local implementation.
    this.schedulerProxy = activator.bind(RemoteScheduler.class)
        .toInstance(this.schedulerProxyTarget);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void start() {
    // Start everything explicitly so we don't start any threads in constructors
    this.localQueue.start();
    this.remoteQueue.start();
    this.forwardingQueue.start();
    this.jobDelegator.start();
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public final JobSchedulingResult createJob(final Job job) {
    // TODO: Find the responsible node by finding the node with the first page of the table
    // FIXME: picking a random node
    final List<RemoteNode> neighbors = this.localNode.getNeighbors();
    final int rnd = (new Random()).nextInt(neighbors.size());

    final RemoteNode node = neighbors.get(rnd);
    job.setResponsibleNode(node);

    try {
      return node.getScheduler().acceptJobAsResponsibleNode(job);
    } catch (final RemoteException e) {
      return new JobSchedulingResult(job, this.localNode.getProxy(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final JobSchedulingResult acceptJobAsResponsibleNode(final Job job) {
    if (this.jobDelegator.offer(job)) {
      return new JobSchedulingResult(job, this.localNode.getProxy(),
                                   JobSchedulingResult.Result.Scheduled);
    } else {
      return new JobSchedulingResult(job, this.localNode.getProxy(),
                                     JobSchedulingResult.Result.Rejected);
    }
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public final TaskSchedulingResult acceptTaskAsInitialNode(final Task task) {

    final TaskSchedulingResult result = new TaskSchedulingResult();

    // If this is the initial node, try to execute the mapreduce locally.
    if (this.isInitialNodeForTask(task)) {
      // Add the mapreduce if the local queue is not full
      result.setTaskScheduled(this.queueLocally(task));
      // If the local queue was full, forward the mapreduce
      if (!result.isTaskScheduled()) {
        result.setTaskScheduled(this.queueForward(task));
      }
    } else {
      // If this is not the initial node, put the mapreduce straight in the forwarding queue
      result.setTaskScheduled(this.queueForward(task));
    }

    return result;
  }

  @Override
  public final void completeTask(final TaskCompletion completion) {
    final RemoteNode reducer = completion.getTask().getParentJob().getReducerNode();
    try {
      reducer.getScheduler().reduceTaskCompletion(completion);
    } catch (final RemoteException e) {
      e.printStackTrace();
    }
  }

  @Override
  public final void reduceTaskCompletion(final TaskCompletion completion) {
    System.out.println("Task complete: ".concat(completion.getResult().toString()));
  }

  private boolean queueLocally(final Task task) {
    return this.localQueue.offer(task);
  }

  private boolean queueForward(final Task task) {
    return this.forwardingQueue.offer(task);
  }

  public final TaskQueue getRemoteQueue() {
    return this.remoteQueue;
  }

  public final RemoteScheduler getProxy() {
    return this.schedulerProxy;
  }

  @Override
  public LocalNode getLocalNode() {
    return this.localNode;
  }

  /**
   * Returns true if the task's {@code initial node} is the same as this node.
   */
  private boolean isInitialNodeForTask(final Task task) {
    return task.getInitialNode().equals(this.localNode.getProxy());
  }
}
