package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;

/**
 *
 */
public interface RemotePeer extends Activatable {

  <T extends Activatable> T getReference(Class<T> cls) throws RemoteException;
}
