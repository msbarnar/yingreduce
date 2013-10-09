package edu.asu.ying.p2p;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.common.RemoteSink;
import edu.asu.ying.mapreduce.database.page.Page;
import edu.asu.ying.mapreduce.mapreduce.scheduling.RemoteScheduler;

/**
 * {@code RemotePeer} is a proxy to a {@link LocalPeer} in another application domain.
 */
public interface RemotePeer extends Remote, Serializable {

  /**
   * Gets the unique network identifier of the remote peer.
   */
  PeerIdentifier getIdentifier() throws RemoteException;

  /**
   * Gets the public interface to the scheduler on the remote peer.
   */
  RemoteScheduler getScheduler() throws RemoteException;

  /**
   * Gets a sink through which the remote peer accepts pages.
   */
  RemoteSink<Page> getPageSink() throws RemoteException;

  /**
   * Gets the current time according to the remote peer.
   */
  long getCurrentTimeMilliseconds() throws RemoteException;
}
