package edu.asu.ying.dfs.server;

import com.google.inject.Inject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.dfs.DFSService;

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
  public PageTransferResponse offer(PageTransfer transfer) throws RemoteException {
    try {
      return service.offer(transfer);
    } catch (IOException e) {
      throw new RemoteException("Uncaught exception accepting page transfer", e);
    }
  }
}
