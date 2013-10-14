package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.WrapperFactory;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskException;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

public final class TaskServiceWrapperFactory implements
                                             WrapperFactory<TaskService, RemoteTaskService> {

  @Override
  public RemoteTaskService create(TaskService target, Activator activator) {
    return new TaskServiceWrapper(target);
  }

  private final class TaskServiceWrapper implements RemoteTaskService {

    private final TaskService server;

    public TaskServiceWrapper(TaskService server) {
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

    @Override
    public int getBackpressure() throws RemoteException {
      return 0;
    }
  }
}