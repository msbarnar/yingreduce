package edu.asu.ying.wellington.mapreduce.task;

import com.google.inject.Inject;

import java.rmi.server.ExportException;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.common.remoting.Remote;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;
import edu.asu.ying.wellington.mapreduce.server.TaskServiceExporter;

/**
 *
 */
public class TaskScheduler implements TaskService {

  private final RemoteTaskService proxy;

  private final DFSService dfsService;

  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final QueueExecutor<Task> forwardingQueue;
  private final QueueExecutor<Task> localQueue;
  private final QueueExecutor<Task> remoteQueue;


  @Inject
  private TaskScheduler(TaskServiceExporter exporter,
                        DFSService dfsService,
                        @Forwarding QueueExecutor<Task> forwardingQueue,
                        @Local QueueExecutor<Task> localQueue,
                        @Remote QueueExecutor<Task> remoteQueue) {

    this.dfsService = dfsService;

    this.localQueue = localQueue;
    this.remoteQueue = remoteQueue;
    this.forwardingQueue = forwardingQueue;

    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }

    // Starting threads in the constructor, but there's nowhere else to start them
    start();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
    // Start everything explicitly so we don't start any threads in constructors
    localQueue.start();
    remoteQueue.start();
    forwardingQueue.start();
  }

  @Override
  public void accept(Task task) throws TaskException {
    // Initial tasks go in the local queue first if available, else everything gets forwarded
    if (isInitialNodeFor(task)) {
      queueLocal(task);
    } else {
      queueForward(task);
    }
  }

  @Override
  public int getBackpressure() {
    return forwardingQueue.size();
  }

  @Override
  public RemoteTaskService asRemote() {
    return proxy;
  }

  private void queueLocal(Task task) throws TaskSchedulingException {
    // Forward to the shortest of {Ql, Qf}
    if (localQueue.size() <= forwardingQueue.size()) {
      // If the local queue won't take it, forward it
      try {
        localQueue.put(task);
      } catch (InterruptedException e) {
        queueForward(task);
      }
    } else {
      queueForward(task);
    }
  }

  private void queueForward(Task task) throws TaskSchedulingException {
    try {
      forwardingQueue.put(task);
    } catch (InterruptedException e) {
      throw new TaskSchedulingException("Forwarding queue was interrupted; no recourse available.");
    }
  }

  /**
   * Returns true if the local DFS service has the correct page of the table specified by the task.
   */
  private boolean isInitialNodeFor(Task task) {
    PageIdentifier pageId = task.getTargetPageID();
    return dfsService.hasPage(pageId);
  }
}
