package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;

/**
 *
 */
public interface RemotePeer extends Activatable {

  <T extends Activatable> T getReference(Class<T> cls) throws RemoteException;
}
