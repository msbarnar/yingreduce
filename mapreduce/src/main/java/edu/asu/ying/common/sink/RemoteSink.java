package edu.asu.ying.common.sink;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 */
public interface RemoteSink<E> extends Remote, Serializable {

  boolean offer(E object) throws RemoteException;

  int offer(Iterable<E> objects) throws RemoteException;
}
