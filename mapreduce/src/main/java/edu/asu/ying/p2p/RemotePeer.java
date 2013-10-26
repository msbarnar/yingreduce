package edu.asu.ying.p2p;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;

/**
 *
 */
public interface RemotePeer extends Activatable {

  String getName() throws RemoteException;

  <T extends Activatable> T getReference(Class<T> cls) throws RemoteException;
}
