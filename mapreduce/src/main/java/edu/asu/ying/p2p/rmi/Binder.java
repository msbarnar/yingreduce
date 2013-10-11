package edu.asu.ying.p2p.rmi;

/**
 * {@code Binder} provides helper functions for binding a class of type {@code K} to a subclass or
 * instance of {@code K}.
 */
public interface Binder<K extends Activatable> {

  /**
   * Binds {@code K} to a specific instance of {@code K}. </p> This binding will only ever provide
   * {@code instance} to fulfill {@code K}.
   */
  <T extends K> K toInstance(T instance);

  <T> WrapperBinder<K> to(T target);

  Binding getBinding();
}