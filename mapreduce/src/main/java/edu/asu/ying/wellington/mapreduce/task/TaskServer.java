package edu.asu.ying.wellington.mapreduce.task;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;
import edu.asu.ying.wellington.mapreduce.net.RemoteTaskService;

/**
 *
 */
public class TaskServer implements TaskService {

  private final LocalNode localNode;

  public TaskServer(LocalNode localNode) {
    this.localNode = localNode;
  }

  @Override
  public void accept(Task task) throws TaskException {
  }

  @Override
  public Class<? extends RemoteTaskService> getWrapper() {
    return TaskServerWrapper.class;
  }

  public final class TaskServerWrapper implements RemoteTaskService {

    private final TaskServer server;

    public TaskServerWrapper(TaskServer server, Activator activator) {
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
}
