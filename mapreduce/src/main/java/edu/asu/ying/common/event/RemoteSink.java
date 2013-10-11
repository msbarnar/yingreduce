package edu.asu.ying.common.event;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activatable;

/**
 *
 */
public interface RemoteSink<E> extends Activatable {

  boolean offer(E object) throws RemoteException;

  int offer(Iterable<E> objects) throws RemoteException;
}
