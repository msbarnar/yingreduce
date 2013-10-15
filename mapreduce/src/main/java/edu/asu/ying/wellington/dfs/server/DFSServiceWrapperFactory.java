package edu.asu.ying.wellington.dfs.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.event.Pipe;
import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.WrapperFactory;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.Page;

/**
 *
 */
public final class DFSServiceWrapperFactory
    implements WrapperFactory<DFSService, RemoteDFSService> {

  @Override
  public RemoteDFSService create(DFSService target, Activator activator) {
    return new DFSServiceWrapper(target);
  }

  private final class DFSServiceWrapper implements RemoteDFSService {

    private final DFSService target;

    private DFSServiceWrapper(DFSService target) {
      this.target = target;
    }

    @Override
    public RemoteSink<Page> getPageDepository() throws RemoteException {
      return Pipe.toSink(target.getPageDepository());
    }
  }
}
