package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Wrapper;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

public final class TaskServiceWrapper
    implements RemoteTaskService, Wrapper<RemoteTaskService, TaskService> {

  private TaskService service;

  @Inject
  public TaskServiceWrapper() {
  }

  @Override
  public void accept(Task task) throws RemoteException {
    try {
      this.service.accept(task);
    } catch (TaskException e) {
      throw new RemoteException("Remote task server threw an exception", e);
    }
  }

  @Override
  public int getBackpressure() throws RemoteException {
    return 0;
  }

  @Override
  public void wrap(TaskService target) throws RemoteException {
    this.service = target;
  }
}