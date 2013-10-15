package edu.asu.ying.wellington.dfs.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.dfs.Page;

/**
 * {@code RemoteDFSService} is the interface to the database server on a remote peer.
 */
public interface RemoteDFSService extends Activatable {

  RemoteSink<Page> getPageDepository() throws RemoteException;
}
