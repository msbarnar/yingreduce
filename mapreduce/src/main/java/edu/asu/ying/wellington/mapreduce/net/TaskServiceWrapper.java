package edu.asu.ying.wellington.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;
import edu.asu.ying.wellington.mapreduce.task.TaskServer;

public final class TaskServiceWrapper implements RemoteTaskService {

  private final TaskServer server;

  public TaskServiceWrapper(TaskServer server, Activator activator) {
    this.server = server;
  }

  @Override
  public void accept(Task task) throws RemoteException {
    try {
      this.server.accept(task);
    } catch (TaskException e) {
      throw new RemoteException("Remote task server threw an exception", e);
    }
  }
}
