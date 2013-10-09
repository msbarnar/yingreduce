package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;

/**
 *
 */
public interface Exportable<T extends Remote> {

  T getProxy();
}
