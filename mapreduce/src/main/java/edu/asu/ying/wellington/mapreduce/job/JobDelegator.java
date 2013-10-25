package edu.asu.ying.wellington.mapreduce.job;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;
import edu.asu.ying.wellington.mapreduce.task.LetterFreqTask;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;
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
    // Find k reducers for the job and set them
    Collection<RemoteNode> reducers = this.findReducers(job);
    job.setReducerNodes(reducers);

    // Since we're at the responsible node, this is also the initial node for task 0.
    // We'll forward this separately, skipping RMI
    Task loopbackTask = null;
    RemoteNode loopbackProxy = loopbackProxyProvider.get();

    // TODO: split job based on number of pages in table
    // TODO: Abstract task class
    final Deque<LetterFreqTask> tasks = new ArrayDeque<>();
    for (int i = 0; i < 40; i++) {
      // Pass the responsible node as a remote proxy so other peers can access it
      final LetterFreqTask task = new LetterFreqTask(job, loopbackProxy, i);
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
      loopbackTask.setInitialNode(loopbackProxy);
      try {
        taskService.accept(loopbackTask);
      } catch (TaskException e) {
        log.log(Level.WARNING, "Exception scheduling task on the local scheduler. Failover is not"
                               + " implemented; part of this job will not run.", e);
        // TODO: Failover
      }
    }

    // Attempt to distribute the tasks to their initial nodes
    while (!tasks.isEmpty()) {
      final Task task = tasks.pop();
      try {
        // Find the initial node by the Task's table ID (table ID + page index)
        final RemoteNode initialNode = nodeLocator.find(task.getTargetPageID().toString());
        task.setInitialNode(initialNode);
        initialNode.getTaskService().accept(task);
      } catch (final IOException e) {
        log.log(Level.WARNING, "Exception scheduling task on remote scheduler. Failover is not"
                               + " implemented; part of this job will not run.", e);
        // TODO: Failover
      }
    }
  }

  // FIXME: Optimize
  private Set<RemoteNode> findReducers(Job job) {
    int numReducers = job.getReducerCount();
    String jobName = job.getName();

    Set<RemoteNode> reducers = new HashSet<>(numReducers);
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
