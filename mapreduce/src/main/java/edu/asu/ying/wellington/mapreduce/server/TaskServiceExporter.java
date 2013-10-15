package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.Exporter;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

public final class TaskServiceExporter
    implements Exporter<TaskService, RemoteTaskService>, RemoteTaskService {

  private final Activator activator;

  private TaskService service;

  @Inject
  private TaskServiceExporter(Activator activator) {
    this.activator = activator;
  }

  @Override
  public RemoteTaskService export(TaskService service) {
    this.service = service;
    try {
      return activator.bind(RemoteTaskService.class, this);
    } catch (ExportException e) {
      // TODO: Logging
      throw new RuntimeException(e);
    }
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
}