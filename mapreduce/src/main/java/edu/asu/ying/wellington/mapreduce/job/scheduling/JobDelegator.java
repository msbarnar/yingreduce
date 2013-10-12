package edu.asu.ying.wellington.mapreduce.job.scheduling;

import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;
import edu.asu.ying.wellington.mapreduce.net.RemoteNode;
import edu.asu.ying.wellington.mapreduce.task.LetterFreqTask;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;

public final class JobDelegator implements Runnable {

  private final LocalNode localNode;

  // Holds unstarted jobs to be split into tasks, each sent to its inital node
  private final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>();

  // One job at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  public JobDelegator(LocalNode localNode) {
    this.localNode = localNode;
  }

  public void start() {
    this.threadPool.submit(this);
  }

  public boolean offer(Job job) {
    return this.jobQueue.offer(job);
  }

  @Override
  public void run() {
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

    this.delegate(job);
  }

  // FIXME: Potentially very slow
  private List<RemoteNode> findReducers(Job job) {
    int numReducers = job.getReducerCount();

    List<RemoteNode> reducers = new ArrayList<>(numReducers);
    for (int i = 0; i < numReducers; i++) {
      reducers.add(this.localNode.findNode(job.getID().toString().concat(Integer.toString(i))));
    }
    return reducers;
  }

  private void delegate(Job job) {
    // Find k reducers for the job and set them
    List<RemoteNode> reducers = this.findReducers(job);
    job.setReducerNodeIDs(reducers);

    // Since we're at the responsible node, this is also the initial node for task 0.
    // We'll forward this separately, skipping RMI
    Task loopbackTask = null;

    // TODO: split job based on number of pages in table
    final Deque<LetterFreqTask> tasks = new ArrayDeque<>();
    for (int i = 0; i < 40; i++) {
      // Pass the responsible node as a remote proxy so other peers can access it
      final LetterFreqTask task = new LetterFreqTask(job, this.localNode.getAsRemote(), i);
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
      loopbackTask.setInitialNode(this.localNode.getAsRemote());
      try {
        this.localNode.getTaskService().accept(loopbackTask);
      } catch (TaskException e) {
        // TODO: Report this somehow
        e.printStackTrace();
      }
    }

    // Attempt to distribute the tasks to their initial nodes
    while (!tasks.isEmpty()) {
      final Task task = tasks.pop();
      try {
        // Find the initial node by the Task's ID (table ID + page index)
        final RemoteNode node = this.localNode.findNode(task.getId().toString());
        // TODO: Logging
        task.setInitialNode(node);
        // FIXME: BROKEN FOR TESTING
        node.getTaskService().accept(task);
      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }
  }
}
