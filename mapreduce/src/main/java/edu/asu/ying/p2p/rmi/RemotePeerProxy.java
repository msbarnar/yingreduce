package edu.asu.ying.p2p.rmi;


import java.rmi.RemoteException;

import edu.asu.ying.common.sink.RemoteSink;
import edu.asu.ying.database.page.Page;
import edu.asu.ying.mapreduce.mapreduce.scheduling.RemoteScheduler;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.RemotePeer;

/**
 * Provides the implementation of {@code RemotePeer} which will be accessible by remote peers when
 * exported. The proxy implementation glues the remote node interface to the concrete local node
 * implementation while implementing the appropriate patterns to be RMI-compatible.
 */
public final class RemotePeerProxy implements RemotePeer {

  private final LocalPeer localPeer;

  public RemotePeerProxy(final LocalPeer localPeer) {
    this.localPeer = localPeer;
  }

  @Override
  public PeerIdentifier getIdentifier() throws RemoteException {
    return this.localPeer.getIdentifier();
  }

  @Override
  public RemoteScheduler getScheduler() throws RemoteException {
    return this.localPeer.getScheduler().getProxy();
  }

  @Override
  public RemoteSink<Page> getPageSink() throws RemoteException {
    return this.localPeer.getPageSink().getProxy();
  }

  @Override
  public long getCurrentTimeMillis() throws RemoteException {
    return System.currentTimeMillis();
  }
}
