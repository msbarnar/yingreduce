package edu.asu.ying.wellington.dfs.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;

/**
 * {@code RemoteDFSService} is the interface to the filesystem server on a remote peer.
 */
public interface RemoteDFSService extends Activatable {

  /**
   * Offers a remote node the opportunity to download a page from the offering node.
   * <p/>
   * The remote node should respond with a {@link PageTransferResponse} indicating the action taken
   * with the page.
   */
  PageTransferResponse offer(PageTransfer transfer) throws RemoteException;

  void notifyPageTransferResult(String transferId, PageTransferResult result)
      throws RemoteException;
}
