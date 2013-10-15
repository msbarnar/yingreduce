package edu.asu.ying.p2p.rmi;

/**
 *
 */
public interface Exporter<T, R> {

  R export(T target);
}
