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
   * Gets the unique network identifier of the remote node.
   */
  PeerIdentifier getIdentifier() throws RemoteException;

  RemoteScheduler getScheduler() throws RemoteException;

  long getTimeMs() throws RemoteException;

  RemoteSink<Page> getPageSink() throws RemoteException;
}
