package edu.asu.ying.wellington.mapreduce.task;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.apache.log4j.Logger;

import java.rmi.server.ExportException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.common.remoting.Remote;
import edu.asu.ying.wellington.LocalNode;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;
import edu.asu.ying.wellington.mapreduce.server.TaskServiceExporter;
import edu.asu.ying.wellington.mapreduce.task.execution.TaskExecutor;

/**
 *
 */
public class TaskScheduler implements TaskService {

  private static final Logger log = Logger.getLogger(TaskScheduler.class);

  private final RemoteTaskService proxy;

  private final DFSService dfsService;

  // Ql and Qr are bounded, but Qf is just a pipe to neighboring peers
  private final QueueExecutor<Task> forwardingQueue;
  private final BlockingDeque<Task> localQueue;
  private final BlockingDeque<Task> remoteQueue;

  private final BlockingDeque<Object> readyQueue;

  private final ExecutorService taskExecutor = Executors.newSingleThreadExecutor();

  private final TaskExecutor executor;

  private final AtomicInteger numLocalTasks = new AtomicInteger(0);

  private final Provider<LocalNode> localNodeProvider;

  @Inject
  private TaskScheduler(TaskServiceExporter exporter,
                        DFSService dfsService,
                        @Forwarding QueueExecutor<Task> forwardingQueue,
                        @Remote BlockingDeque<Task> remoteQueue,
                        @Local BlockingDeque<Object> readyQueue,
                        TaskExecutor executor,
                        Provider<LocalNode> localNodeProvider) {

    this.dfsService = dfsService;

    this.localQueue = new LinkedBlockingDeque<>();
    this.remoteQueue = remoteQueue;
    this.readyQueue = readyQueue;
    this.forwardingQueue = forwardingQueue;

    this.executor = executor;

    this.localNodeProvider = localNodeProvider;

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
    forwardingQueue.start();

    taskExecutor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          readyQueue.take();
          if (localQueue.size() >= remoteQueue.size()) {
            executor.execute(localQueue.take());
          } else {
            executor.execute(remoteQueue.take());
          }
          taskExecutor.submit(this);
        } catch (InterruptedException ignored) {
        }
      }
    });
  }

  @Override
  public void stop() {
    forwardingQueue.stop();
  }

  @Override
  public void accept(Task task) throws TaskException {
    // Initial tasks go in the local queue first if available, else everything gets forwarded
    /*if (isInitialNodeFor(task)) {
      queueLocal(task);
    } else {
      queueForward(task);
    }*/
    // FIXME: Experiment: random scheduling
    queueLocal(task);
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
    // FIXME: Experiment: random scheduling
    //if (localQueue.size() <= forwardingQueue.size()) {
      // If the local queue won't take it, forward it
      localQueue.add(task);
      readyQueue.add(new Object());
      System.out.println(localNodeProvider.get().getName() + " - Local Tasks: " + numLocalTasks.incrementAndGet());

      //log.info("Queue local: " + task.getTargetPageID());
    // FIXME: Experiment: random scheduling
    /*} else {
      queueForward(task);
    }*/
  }

  private void queueForward(Task task) throws TaskSchedulingException {
    forwardingQueue.add(task);
    //log.info("Queue forward: " + task.getTargetPageID());
  }

  /**
   * Returns true if the local DFS service has the correct page of the table specified by the task.
   */
  private boolean isInitialNodeFor(Task task) {
    PageName pageId = task.getTargetPageID();
    return dfsService.hasPage(pageId);
  }
}
