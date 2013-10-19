package edu.asu.ying.wellington.dfs.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;

/**
 * {@code RemoteDFSService} is the interface to the database server on a remote peer.
 */
public interface RemoteDFSService extends Activatable {

  PageTransferResult offer(PageTransfer transfer) throws RemoteException;
}
