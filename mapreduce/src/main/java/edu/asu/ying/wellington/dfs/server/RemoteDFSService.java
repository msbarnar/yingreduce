package edu.asu.ying.wellington.dfs.server;

import com.healthmarketscience.rmiio.RemoteInputStream;

import java.rmi.RemoteException;
import java.util.List;

import javax.annotation.Nullable;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.PageName;

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

  RemoteInputStream getRemoteInputStream(PageName name) throws RemoteException;

  void ping(RemoteNode pinger) throws RemoteException;

  List<RemoteNode> getResponsibleNodesFor(PageName name) throws RemoteException;
}
