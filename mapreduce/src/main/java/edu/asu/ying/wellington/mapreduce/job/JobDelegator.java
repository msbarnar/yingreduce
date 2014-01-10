package edu.asu.ying.wellington.mapreduce.job;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.wellington.NodeLocator;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.InvalidPathException;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.Path;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;
import edu.asu.ying.wellington.mapreduce.task.TaskIdentifier;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

public final class JobDelegator extends QueueExecutor<Job> {

  private static final Logger log = Logger.getLogger(JobDelegator.class.getName());

  private final Provider<RemoteNode> loopbackProxyProvider;
  private final NodeLocator nodeLocator;
  private final TaskService taskService;

  @Inject
  private JobDelegator(@Local Provider<RemoteNode> loopbackProxyProvider,
                       NodeLocator nodeLocator,
                       TaskService taskService) {

    this.loopbackProxyProvider = loopbackProxyProvider;
    this.nodeLocator = nodeLocator;
    this.taskService = taskService;
  }

  @Override
  protected void process(Job job) {
    job.setStartTime(System.currentTimeMillis());
    // Find k reducers for the job and set them
    Set<RemoteNode> reducers = findReducers(job);
    job.setReducerNodes(reducers);

    // Since we're at the responsible node, this is also the initial node for task 0.
    // We'll forward this separately, skipping RMI
    Task loopbackTask = null;
    RemoteNode loopbackProxy = loopbackProxyProvider.get();

    // Get number of pages in file
    Path filePath = null;
    try {
      filePath = new Path(job.getTableName());

      int i;
      for (i = 0; ; i++) {
        PageName pageName = PageName.create(filePath, i);
        RemoteNode node = nodeLocator.find(pageName.toString());
        if (!node.getDFSService().hasPage(pageName)) {
          log.info("Number of pages in file " + filePath.toString() + " is " + i);
          break;
        }
      }

      job.setNumTasks(i);

    } catch (IOException e) {
      // This whole codebase is an embarrassment
      throw new RuntimeException(e);
    }

    // Delegate tasks
    Deque<Task> tasks = new ArrayDeque<>();
    for (int i = 0; i < job.getNumTasks(); i++) {
      Task task = null;
      try {
        task = new Task(job, TaskIdentifier.random(), PageName.create(new Path(job.getTableName()),
                                                                      i));
      } catch (InvalidPathException e) {
        log.severe("Bad table name for new task: " + job.getTableName());
        continue;
      }
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

    log.info("Created " + job.getNumTasks() + " tasks for file " + job.getTableName());

    // Now that the number of tasks is known for the job, start with the loopback task
    if (loopbackTask != null) {
      loopbackTask.setInitialNode(loopbackProxy);
      try {
        taskService.accept(loopbackTask);
      } catch (TaskException e) {
        log.log(Level.WARNING, "Exception scheduling task on the local scheduler. Failover is not"
                               + " implemented; part of this job will not run.", e);
        // FIXME: Failover
      }
    }

    int numDelegated = 1;
    // Attempt to distribute the tasks to their initial nodes
    while (!tasks.isEmpty()) {
      final Task task = tasks.pop();
      try {
        // Find the initial node by the Task's table ID (table ID + page index)
        final RemoteNode initialNode = nodeLocator.find(task.getTargetPageID().toString());
        task.setInitialNode(initialNode);
        initialNode.getTaskService().accept(task);
        ++numDelegated;
      } catch (final IOException e) {
        log.log(Level.WARNING, "Exception scheduling task on remote scheduler. Failover is not"
                               + " implemented; part of this job will not run.", e);
        // FIXME: Failover
      }
    }

    log.info("Delegated " + numDelegated + " tasks for file " + job.getTableName());
  }

  // FIXME: Optimize
  private Set<RemoteNode> findReducers(Job job) {
    int numReducers = job.getReducerCount();
    String jobName = job.getName();

    // Linked to retain order
    Set<RemoteNode> reducers = new LinkedHashSet<>(numReducers);
    for (int i = 0; i < numReducers; i++) {
      try {
        reducers.add(nodeLocator.find(jobName.concat(Integer.toString(i))));
      } catch (IOException e) {
        log.log(Level.WARNING, "Exception getting reference to reducer node. Failover is not"
                               + " implemented; this job will run with fewer reducers than"
                               + " configured.", e);
        // TODO: Failover
      }
    }
    return reducers;
  }
}
