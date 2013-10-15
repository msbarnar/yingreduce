package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;

import edu.asu.ying.common.event.Pipe;
import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.p2p.rmi.Wrapper;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.Page;

/**
 *
 */
public final class DFSServiceWrapper
    implements Wrapper<RemoteDFSService, DFSService>, RemoteDFSService {

  private DFSService service;

  @Inject
  private DFSServiceWrapper() {
  }

  @Override
  public RemoteSink<Page> getPageDepository() throws RemoteException {
    return Pipe.toSink(service.getPageDepository());
  }

  @Override
  public void wrap(DFSService target) throws RemoteException {
    this.service = target;
  }
}
