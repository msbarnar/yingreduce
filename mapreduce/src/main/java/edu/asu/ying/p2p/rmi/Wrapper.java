package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;

/**
 *
 */
public interface Wrapper<R extends Activatable, T> extends Activatable {

  void wrap(T target) throws RemoteException;
}
