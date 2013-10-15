package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.WrapperFactory;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.server.DFSServiceWrapperFactory;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 *
 */
public final class RemoteNodeWrapperFactory implements WrapperFactory<NodeServer, RemoteNode> {

  private final JobService jobService;
  private final TaskService taskService;
  private final DFSService dfsService;

  @Inject
  private RemoteNodeWrapperFactory(JobService jobService, TaskService taskService,
                                   DFSService dfsService) {

    this.jobService = jobService;
    this.taskService = taskService;
    this.dfsService = dfsService;
  }

  @Override
  public RemoteNode create(NodeServer target, Activator activator) {
    return new RemoteNodeWrapper(target, activator, jobService, taskService, dfsService);
  }

  private final class RemoteNodeWrapper implements RemoteNode {

    private final LocalNode localNode;
    private final RemoteJobService jobServiceProxy;
    private final RemoteTaskService taskServiceProxy;
    private final RemoteDFSService dfsServiceProxy;

    private RemoteNodeWrapper(LocalNode localNode, Activator activator,
                              JobService jobService, TaskService taskService,
                              DFSService dfsService) {

      this.localNode = localNode;

      try {
        this.jobServiceProxy = activator.bind(RemoteJobService.class)
            .to(jobService)
            .wrappedBy(new JobServiceWrapperFactory());

        this.taskServiceProxy = activator.bind(RemoteTaskService.class)
            .to(taskService)
            .wrappedBy(new TaskServiceWrapperFactory());

        this.dfsServiceProxy = activator.bind(RemoteDFSService.class)
            .to(dfsService)
            .wrappedBy(new DFSServiceWrapperFactory());

      } catch (ExportException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public NodeIdentifier getIdentifier() throws RemoteException {
      return localNode.getID();
    }

    @Override
    public RemoteJobService getJobService() throws RemoteException {
      return jobServiceProxy;
    }

    @Override
    public RemoteTaskService getTaskService() throws RemoteException {
      return taskServiceProxy;
    }

    @Override
    public RemoteDFSService getDFSService() throws RemoteException {
      return dfsServiceProxy;
    }
  }
}
