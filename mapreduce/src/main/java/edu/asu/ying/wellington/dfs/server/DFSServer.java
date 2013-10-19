package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.rmi.server.ExportException;

import edu.asu.ying.wellington.dfs.DFSService;

/**
 *
 */
public class DFSServer implements DFSService {

  private final RemoteDFSService proxy;

  @Inject
  private DFSServer(DFSServiceExporter exporter) {
    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void start() {
  }

  @Override
  public RemoteDFSService asRemote() {
    return proxy;
  }
}
