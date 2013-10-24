package edu.asu.ying.common.event;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;

/**
 *
 */
public interface RemoteSink<E> extends Activatable {

  void accept(E object) throws RemoteException;
}
