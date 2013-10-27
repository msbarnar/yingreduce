package edu.asu.ying.common.event;

import java.rmi.RemoteException;

import edu.asu.ying.rmi.Activatable;

/**
 *
 */
public interface RemoteSink<E> extends Activatable {

  void accept(E object) throws RemoteException;
}
