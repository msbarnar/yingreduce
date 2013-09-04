package edu.asu.ying.mapreduce.mapreduce.job;

import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.mapreduce.task.LetterFreqTask;
import edu.asu.ying.mapreduce.mapreduce.task.TaskHistory;
import edu.asu.ying.mapreduce.node.kad.KadNodeIdentifier;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.p2p.RemoteNode;

public final class JobDelegatorImpl implements JobDelegator, Runnable {

  private final LocalNode localNode;

  private final BlockingQueue<Job> queue;

  // One job at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  public JobDelegatorImpl(final LocalNode localNode, final BlockingQueue<Job> queue) {
    this.localNode = localNode;
    this.queue = queue;
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
      job = this.queue.take();
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
    System.out.println("Delegating job ".concat(job.getId().toString()));

    final Deque<LetterFreqTask> tasks = new ArrayDeque<>();
    for (int i = 0; i < 10; i++) {
      final LetterFreqTask task = new LetterFreqTask(i);
      task.getHistory().append(new TaskHistory.Entry(this.localNode.getIdentifier(),
                                                     TaskHistory.NodeRole.Responsible,
                                                     TaskHistory.SchedulerAction.None));
      tasks.push(task);
    }

    final List<RemoteNode> neighbors = this.localNode.getNeighbors();

    while (!tasks.isEmpty()) {
      final LetterFreqTask task = tasks.pop();
      try {
        final RemoteNode node = this.localNode.findNode(
            new KadNodeIdentifier(task.getId().toString()));
        task.setInitialNodeURI(node.getIdentifier());
        node.getActivator().getReference(Scheduler.class, null).acceptTask(task);
      } catch (final RemoteException e) {
        e.printStackTrace();
      }
    }
  }
}
