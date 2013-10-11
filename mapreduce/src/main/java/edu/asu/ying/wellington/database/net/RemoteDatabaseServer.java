package edu.asu.ying.wellington.database.net;

import java.rmi.RemoteException;

import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.p2p.rmi.Activatable;
import edu.asu.ying.wellington.database.page.Page;

/**
 * {@code RemoteDatabaseServer} is the interface to the database server on a remote peer.
 */
public interface RemoteDatabaseServer extends Activatable {

  RemoteSink<Page> getPageSink() throws RemoteException;
}
