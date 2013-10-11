package edu.asu.ying.database.net;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.database.page.Page;

/**
 * {@code RemoteDatabaseServer} is the interface to the database server on a remote peer.
 */
public interface RemoteDatabaseServer extends Remote, Serializable {

  RemoteSink<Page> getPageSink() throws RemoteException;
}
