package edu.asu.ying.wellington.mapreduce.server;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import edu.asu.ying.wellington.LocalNode;
import edu.asu.ying.wellington.dfs.InvalidPathException;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.Path;
import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public class ReducerImpl implements Reducer {

  private static final Logger log = Logger.getLogger(RemoteReducerImpl.class);

  private final LocalNode node;
  private final Map<String, Map<WritableChar, WritableInt>> reductions = new HashMap<>();

  private final Map<String, Set<PageName>> pendingTasks = new HashMap<>();
  private final Map<String, Job> jobs = new HashMap<>();
  private final Map<String, Timer> completionWaiters = new HashMap<>();

  private static long numJobsCompleted = 0;
  private static long startTime = 0;


  public ReducerImpl(LocalNode node) {
    this.node = node;
  }

  @Override
  public void collect(Task task, WritableChar key, WritableInt value, boolean isFinished) {
    if (startTime == 0) {
      startTime = System.currentTimeMillis();
    }
    // Populate the task list
    onTask(task);
    if (isFinished) {
      completeTask(task);
    }

    // Allow a task to be completed without additional results
    if (key == null || value == null) {
      return;
    }

    // Collect reductions by key for each job
    String jobName = task.getParentJob().getName();
    Map<WritableChar, WritableInt> reduction = reductions.get(jobName);
    if (reduction == null) {
      reduction = new HashMap<>();
      reductions.put(jobName, reduction);
    }
    WritableInt preexisting = reduction.get(key);
    if (preexisting != null) {
      preexisting.set(preexisting.get() + value.get());
    } else {
      preexisting = value;
    }
    reduction.put(key, preexisting);
  }

  @Override
  public void commit() {
  }

  /**
   * Gets the incomplete tasks for a job or, if that job doesn't exist, populates the list of
   * tasks to expect.
   */
  private Set<PageName> getPendingTasks(Task task) {
    Job job = task.getParentJob();

    Set<PageName> pending = pendingTasks.get(job.getName());
    if (pending == null) {
      log.info(String.format("New reduction job: %s\t%d tasks", task.getParentJob().getTableName(),
                             job.getNumTasks()));
      pending = new HashSet<>();
      pendingTasks.put(job.getName(), pending);
      for (int i = 0; i < job.getNumTasks(); i++) {
        try {
          pending.add(PageName.create(new Path(job.getTableName()), i));
        } catch (InvalidPathException e) {
          log.error("Invalid path adding pending tasks to reducer", e);
        }
      }
    }

    return pending;
  }

  private void onTask(Task task) {
    getPendingTasks(task);
  }

  private void completeTask(Task task) {
    //log.info("Task complete: " + task.getTargetPageID());
    Set<PageName> pending = getPendingTasks(task);
    pending.remove(task.getTargetPageID());
    if (pending.isEmpty()) {
      completeJob(task.getParentJob());
    }
  }

  private void completeJob(Job job) {
    String jobName = job.getName();

    log.info("Reduction complete on " + node.getName() + ": " + job.getTableName());
    ++numJobsCompleted;
    System.out.println(numJobsCompleted + "," + (System.currentTimeMillis() - startTime));

    Timer timer = completionWaiters.get(jobName);
    if (timer != null) {
      timer.cancel();
      completionWaiters.remove(jobName);
    }
    pendingTasks.remove(jobName);

    try {
      job.getResponsibleNode().getJobService().completeReduction(node.asRemote(), job);
    } catch (RemoteException e) {
      log.error("Reducer tried to tell responsible node it was done, responsible node unreachable",
                e);
    }
  }
}
