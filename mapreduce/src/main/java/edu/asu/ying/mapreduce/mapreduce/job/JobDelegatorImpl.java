package edu.asu.ying.mapreduce.mapreduce.job;

import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.mapreduce.task.LetterFreqTask;
import edu.asu.ying.mapreduce.mapreduce.task.TaskHistory;
import edu.asu.ying.p2p.node.kad.KadNodeIdentifier;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.p2p.RemoteNode;

public final class JobDelegatorImpl implements JobDelegator, Runnable {

  private final LocalNode localNode;

  // Holds unstarted jobs to be split into tasks, each sent to its inital node
  private final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>();

  // One job at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  public JobDelegatorImpl(final LocalNode localNode) {
    this.localNode = localNode;
  }

  @Override
  public final void start() {
    this.threadPool.submit(this);
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
    System.out.println("Delegating job ".concat(job.getID().toString()));

    final Deque<LetterFreqTask> tasks = new ArrayDeque<>();
    for (int i = 0; i < 10; i++) {
      // Pass the responsible node as a remote proxy so other peers can access it
      final LetterFreqTask task = new LetterFreqTask(job, this.localNode.getProxy(), i);
      // TODO: Add to history

      tasks.push(task);
    }

    final List<RemoteNode> neighbors = this.localNode.getNeighbors();

    // Attempt to distribute the tasks to their initial nodes
    while (!tasks.isEmpty()) {
      final LetterFreqTask task = tasks.pop();
      try {
        final RemoteNode node = this.localNode.findNode(
            new KadNodeIdentifier(task.getId().toString()));
        task.setInitialNode(node);
        node.getScheduler().delegateTask(task);
      } catch (final RemoteException e) {
        e.printStackTrace();
      }
    }
  }
}
