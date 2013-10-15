package edu.asu.ying.p2p.rmi;

/**
 *
 */
public interface Wrapper<T extends Activatable> {

  T getProxy();
}
