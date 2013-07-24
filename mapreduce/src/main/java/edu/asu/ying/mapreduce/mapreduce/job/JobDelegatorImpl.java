package edu.asu.ying.mapreduce.mapreduce.job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.asu.ying.mapreduce.net.LocalNode;

public final class JobDelegatorImpl implements JobDelegator, Runnable {

  private final BlockingQueue<Job> queue;

  // One job at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  public JobDelegatorImpl(final BlockingQueue<Job> queue) {
    this.queue = queue;
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

  }
}
