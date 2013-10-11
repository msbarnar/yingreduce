package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.server.ExportException;

/**
 *
 */
public interface Activator {

  /**
   * A {@code Binding} fulfills a request for an object of type {@code K} by providing an instance
   * of {@code K} or one of its subclasses.
   */
  interface Binding<K extends Activatable> {

    /**
     * Provides a fully exported {@link Remote} proxy of type {@code K}.
     */
    K getReference();
  }

  /**
   * {@code Binder} provides helper functions for binding a class of type {@code K} to a subclass or
   * instance of {@code K}.
   */
  interface Binder<K extends Activatable> {

    /**
     * Binds {@code K} to a specific instance of {@code K}. </p> This binding will only ever provide
     * {@code instance} to fulfill {@code K}.
     */
    K toInstance(K instance);

    <V extends K> ViaBinder<K> to(Class<V> target);

    Binding getBinding();
  }

  interface ViaBinder<K extends Activatable> {

    /**
     * Binds {@code K} to a specific instance of {@code K}. </p> This binding will only ever provide
     * {@code instance} to fulfill {@code K}.
     */
    K toInstance(Object instance) throws ExportException;
  }

  <V extends Activatable> Binder<V> bind(Class<V> type);

  <K extends Activatable> K getReference(Class<? extends Activatable> cls);

  int getPort();
}
