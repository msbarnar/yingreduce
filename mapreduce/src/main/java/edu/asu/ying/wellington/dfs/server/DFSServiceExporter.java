package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import com.healthmarketscience.rmiio.RemoteInputStream;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.List;

import javax.annotation.Nullable;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageName;

/**
 *
 */
public final class DFSServiceExporter
    implements Exporter<DFSService, RemoteDFSService>, RemoteDFSService {

  private final Activator activator;
  // Informed when another node pings us so it can update its knowledge of which nodes are up
  private final PageReplicator replicator;

  private DFSService service;

  @Inject
  private DFSServiceExporter(Activator activator, PageReplicator replicator) {
    this.activator = activator;
    this.replicator = replicator;
  }

  @Override
  public RemoteDFSService export(DFSService target) throws ExportException {
    this.service = target;
    return activator.bind(RemoteDFSService.class, this);
  }

  @Override
  public PageTransferResponse offer(PageTransfer transfer) throws RemoteException {
    try {
      return service.offer(transfer);
    } catch (IOException e) {
      throw new RemoteException("Uncaught exception accepting page transfer", e);
    }
  }

  @Override
  public void notifyTransferResult(String transferId, @Nullable Throwable exception)
      throws RemoteException {

    service.notifyTransferResult(transferId, exception);
  }

  @Override
  public RemoteInputStream getRemoteInputStream(PageName name) throws RemoteException {
    try {
      return service.provideRemoteInputStream(name);
    } catch (IOException e) {
      throw new RemoteException("Remote node threw an exception providing page input stream", e);
    }
  }

  @Override
  public void ping(RemoteNode pinger) throws RemoteException {
    replicator.ping(pinger);
  }

  @Override
  public List<RemoteNode> getResponsibleNodesFor(PageName name) throws RemoteException {
    return replicator.getResponsibleNodesFor(name);
  }
}
