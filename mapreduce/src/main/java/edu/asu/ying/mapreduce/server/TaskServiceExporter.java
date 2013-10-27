package edu.asu.ying.mapreduce.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.mapreduce.task.Task;
import edu.asu.ying.mapreduce.task.TaskException;
import edu.asu.ying.mapreduce.task.TaskService;

public final class TaskServiceExporter
    implements Exporter<TaskService, RemoteTaskService>, RemoteTaskService {

  private final Activator activator;

  private TaskService service;

  @Inject
  private TaskServiceExporter(Activator activator) {
    this.activator = activator;
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
    return service.getBackpressure();
  }

  @Override
  public RemoteTaskService export(TaskService service) throws ExportException {
    this.service = service;
    return activator.bind(RemoteTaskService.class, this);
  }
}