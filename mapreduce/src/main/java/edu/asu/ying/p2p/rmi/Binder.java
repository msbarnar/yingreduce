package edu.asu.ying.p2p.rmi;

import java.rmi.server.ExportException;

/**
 * {@code Binder} provides helper functions for binding a class of type {@code K} to a subclass or
 * instance of {@code K}.
 */
interface Binder<K extends Activatable> {

  /**
   * Binds {@code K} to a specific instance of {@code K}. </p> This binding will only ever provide
   * {@code instance} to fulfill {@code K}.
   */
  <T extends K> K toInstance(T instance);

  ViaBinder<K> to(Class<?> target);

  Binding getBinding();
}

interface ViaBinder<K extends Activatable> {

  /**
   * Makes {@code instance} the target of the {@code via} binding. </p> When the bound type {@code
   * K} of the binding is requested, this binding will provide it by instantiating its wrapper class
   * with {@code instance} as the parameter.
   */
  <T> K to(T instance) throws ExportException;
}