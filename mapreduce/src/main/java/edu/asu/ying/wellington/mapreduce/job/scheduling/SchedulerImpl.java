package edu.asu.ying.wellington.mapreduce.job.scheduling;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.kad.KadPeerIdentifier;
import edu.asu.ying.p2p.rmi.RemoteImportException;

/**
 * The {@code SchedulerImpl} is responsible for accepting a {@link Task} from another node (or from
 * the local node, if the mapreduce was started locally) and queuing it for execution in one of the
 * following queues, deferring to {@code forwarding} if {@code local} is full. <ol> <li>{@code
 * Local} - mapreduce are executed directly on the local node.</li> <li>{@code Forwarding} -
 * mapreduce are sent to either the local node's {@code remote} queue, or to the forwarding queue of
 * a random immediately-connected node.</li> </ol> Once the scheduler has placed the mapreduce in a
 * queue, the mapreduce is taken over by that queue's {@link TaskQueue}.
 */
public final class SchedulerImpl implements LocalScheduler {

  private static final int MAX_QUEUE_SIZE = 1;
  // The node on which this scheduler is running
  private final LocalPeer localPeer;
  // The job delegator accepts unstarted jobs, splits them into tasks, and delegates each task to
  // its initial node.
  private final JobDelegator jobDelegator;
  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final TaskQueue forwardingQueue;
  private final TaskQueue localQueue = new LocalTaskQueue(this);
  private final TaskQueue remoteQueue = new RemoteTaskQueue(this);
  // TODO: Write a reducer class
  private final
  Map<TaskID, List<Serializable>> reductions = new HashMap<>();


  public SchedulerImpl(final LocalPeer localPeer) {

    this.localPeer = localPeer;

    // Set up forwarding queue with node reference so it can find neighbors
    this.forwardingQueue = new ForwardingTaskQueue(this, this.localPeer);

    // Set up the delegator that splits jobs into tasks and sends them to initial nodes
    this.jobDelegator = new JobDelegatorImpl(this.localPeer);
  }

  /**
   * {@inheritDoc}
   */
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
  public final JobSchedulingResult createJob(final Job job) {
    // Get the responsible node by finding the node with the first page of the table
    RemotePeer node = null;
    try {
      node = this.localPeer.findPeer(
          new KadPeerIdentifier(job.getTableID().toString().concat("0")));
      job.setResponsibleNode(node);

    } catch (final PeerNotFoundException | RemoteImportException e) {
      // TODO: Logging
      e.printStackTrace();
      return new JobSchedulingResult(job, this.localPeer.getProxy(), e);
    }

    try {
      //return node.getScheduler().acceptJobAsResponsibleNode(job);
      // FIXME: BROKEN FOR TESTING
      throw new RemoteException();

    } catch (final RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
      return new JobSchedulingResult(job, this.localPeer.getProxy(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public final JobSchedulingResult acceptJobAsResponsibleNode(final Job job) {
    if (this.jobDelegator.offer(job)) {
      return new JobSchedulingResult(job, this.localPeer.getProxy(),
                                     JobSchedulingResult.Result.Scheduled);
    } else {
      return new JobSchedulingResult(job, this.localPeer.getProxy(),
                                     JobSchedulingResult.Result.Rejected);
    }
  }

  public final TaskSchedulingResult acceptInitialTask(final Task task) {

    if (this.isInitialNodeForTask(task)) {
      if (this.localQueue.size() <= this.forwardingQueue.size()) {
        return new TaskSchedulingResult(this.localQueue.offer(task));
      }
    }
    return new TaskSchedulingResult(this.forwardingQueue.offer(task));
  }

  /**
   * {@inheritDoc}
   */
  public final TaskSchedulingResult acceptTask(final Task task) {
    // If this is the initial node, try to execute the task locally.
    // If this is not the initial node, put the task in the shortest of the remote and forwarding
    // queues.
    if (this.remoteQueue.size() <= this.forwardingQueue.size()) {
      return new TaskSchedulingResult(this.remoteQueue.offer(task));
    } else {
      return new TaskSchedulingResult(this.forwardingQueue.offer(task));
    }
  }

  /**
   * Pass a completed task to that task's reducer.
   */
  public final void completeTask(final TaskCompletion completion) {
    final RemotePeer reducer = completion.getTask().getParentJob().getReducerNode();
    try {
      //reducer.getScheduler().reduceTaskCompletion(completion);
      // FIXME: BROKEN FOR TESTING
      throw new RemoteException();
    } catch (final RemoteException e) {
      e.printStackTrace();
    }
  }

  // FIXME: Kill this method
  public final void reduceTaskCompletion(final TaskCompletion completion) {
    // Collect results
    List<Serializable> results = this.reductions.get(completion.getTask().getParentJob().getID());
    if (results == null) {
      results = new ArrayList<>();
      this.reductions.put(completion.getTask().getParentJob().getID(), results);
    }

    results.add(completion.getResult());

    if (results.size() >= completion.getTask().getParentJob().getNumTasks()) {
      final Map<Character, Integer> fin = new TreeMap<>();

      for (final Serializable result : results) {
        final Map<Character, Integer> res = (Map<Character, Integer>) result;
        for (final Character c : res.keySet()) {
          Integer exist = fin.get(c);
          if (exist == null) {
            exist = 0;
          }
          fin.put(c, exist + res.get(c));
        }
      }

      System.out.println(String.format("%d ms: %s",
                                       completion.getTask().getParentJob().getTimeElapsed(),
                                       fin.toString()));
    }
  }

  public final TaskQueue getRemoteQueue() {
    return this.remoteQueue;
  }

  public final TaskQueue getForwardQueue() {
    return this.forwardingQueue;
  }

  public LocalPeer getLocalPeer() {
    return this.localPeer;
  }

  /**
   * Returns true if the task's {@code initial node} is the same as this node.
   */
  private boolean isInitialNodeForTask(final Task task) {
    return task.getInitialNode().equals(this.localPeer.getProxy());
  }
}
