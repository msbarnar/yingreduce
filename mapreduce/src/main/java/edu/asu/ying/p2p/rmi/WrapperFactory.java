package edu.asu.ying.p2p.rmi;

/**
 *
 */
public interface WrapperFactory<T, K> {

  K create(T target, Activator activator);
}
