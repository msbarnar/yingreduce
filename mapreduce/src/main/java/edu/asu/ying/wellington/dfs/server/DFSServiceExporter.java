package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.event.Pipe;
import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageMetadata;

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
  public RemoteSink<PageMetadata> getPageDepository() throws RemoteException {
    return Pipe.toSink(service.getPageDepository());
  }

  @Override
  public RemoteDFSService export(DFSService target) throws ExportException {
    this.service = target;
    return activator.bind(RemoteDFSService.class, this);
  }
}
