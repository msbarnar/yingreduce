package edu.asu.ying.wellington.mapreduce.job.scheduling;

import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.kad.KadPeerIdentifier;
import edu.asu.ying.p2p.rmi.RemoteImportException;

public final class JobDelegatorImpl implements JobDelegator, Runnable {

  private final LocalPeer localPeer;

  // Holds unstarted jobs to be split into tasks, each sent to its inital node
  private final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<Job>();

  // One job at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  public JobDelegatorImpl(final LocalPeer localPeer) {
    this.localPeer = localPeer;
  }

  @Override
  public final void start() {
    this.threadPool.submit(this);
  }

  @Override
  public final boolean offer(final Job job) {
    return this.jobQueue.offer(job);
  }

  @Override
  public final void run() {
    // Run forever
    this.threadPool.submit(this);

    Job job = null;
    try {
      // Blocks until available
      job = this.jobQueue.take();
    } catch (final InterruptedException e) {
      // TODO: Logging
      e.printStackTrace();
    }

    if (job == null) {
      return;
    }

    this.delegateJob(job);
  }

  private void delegateJob(final Job job) {
    // TODO: Logging
    //System.out.println("Delegating job ".concat(job.getID().toString()));

    // Find the reducer node first given the job ID
    // FIXME: Find n reducers and divide keyspace among them
    try {
      final RemotePeer reducer = this.localPeer.findPeer(
          new KadPeerIdentifier(job.getID().toString()));
      job.setReducerNode(reducer);
      // Get a reference start time from the reducer so it can accurately time the job
      job.setStartTime(reducer);

    } catch (final PeerNotFoundException | RemoteImportException e) {
      // FIXME: don't runtime exception
      throw new RuntimeException(e);
    }

    // Since we're at the responsible node, this is also the initial node for task 0.
    // We'll forward this separately, skipping RMI
    Task loopbackTask = null;

    // TODO: split job based on number of pages in table
    final Deque<LetterFreqTask> tasks = new ArrayDeque<>();
    for (int i = 0; i < 40; i++) {
      // Pass the responsible node as a remote proxy so other peers can access it
      final LetterFreqTask task = new LetterFreqTask(job, this.localPeer.getProxy(), i);
      // Jobs are delegated at the responsible node, defined as the node bearing the first page of
      // data. We know we are already at T0's initial node, then.
      // Save a little overhead by routing the first task locally instead of through RMI
      // We still need to know the total number of jobs before delegating this one, so hang on to it
      if (i == 0) {
        loopbackTask = task;
      } else {
        tasks.push(task);
      }
    }

    job.setNumTasks(tasks.size() + 1);

    // Now that the number of tasks is known for the job, start with the loopback task
    if (loopbackTask != null) {
      loopbackTask.setInitialNode(this.localPeer.getProxy());
      // FIXME: BROKEN FOR TESTING
      //this.localPeer.getScheduler().acceptInitialTask(loopbackTask);
    }

    // Attempt to distribute the tasks to their initial nodes
    while (!tasks.isEmpty()) {
      final Task task = tasks.pop();
      try {
        // Find the initial node by the Task's ID (table ID + page index)
        final RemotePeer node = this.localPeer.findPeer(
            new KadPeerIdentifier(task.getId().toString()));
        // TODO: Logging
        task.setInitialNode(node);
        // FIXME: BROKEN FOR TESTING
        //node.getScheduler().acceptInitialTask(task);
      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }
  }
}
