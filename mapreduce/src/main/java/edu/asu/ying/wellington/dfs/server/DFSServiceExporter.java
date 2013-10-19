package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.wellington.dfs.DFSService;

/**
 *
 */
public final class DFSServiceExporter
    implements Exporter<DFSService, RemoteDFSService>, RemoteDFSService {

  private final Activator activator;
  private DFSService service;

  @Inject
  private DFSServiceExporter(Activator activator) {
    this.activator = activator;
  }

  @Override
  public RemoteDFSService export(DFSService target) throws ExportException {
    this.service = target;
    return activator.bind(RemoteDFSService.class, this);
  }

  @Override
  public PageTransferResult offer(PageTransfer transfer) throws RemoteException {
    return null;
  }
}
