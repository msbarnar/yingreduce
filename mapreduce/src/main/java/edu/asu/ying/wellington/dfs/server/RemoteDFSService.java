package edu.asu.ying.wellington.dfs.server;

import java.rmi.RemoteException;

import javax.annotation.Nullable;

import edu.asu.ying.common.remoting.Activatable;

/**
 * {@code RemoteDFSService} is the interface to the filesystem server on a remote peer.
 */
public interface RemoteDFSService extends Activatable {

  /**
   * @see edu.asu.ying.wellington.dfs.DFSService#offer(PageTransfer)
   */
  PageTransferResponse offer(PageTransfer transfer) throws RemoteException;

  /**
   * @see edu.asu.ying.wellington.dfs.DFSService#notifyTransferResult(String, Throwable)
   */
  void notifyTransferResult(String transferId, @Nullable Throwable exception)
      throws RemoteException;
}
