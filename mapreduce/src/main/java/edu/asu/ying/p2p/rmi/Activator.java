package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;

/**
 *
 */
public interface Activator {

  /**
   * Creates a binding for the class {@code cls}. </p> The binding is not active until it is
   * assigned to a target.
   */
  <T extends Activatable> Binder<T> bind(Class<T> cls);

  /**
   * Gets a {@link Remote} proxy referencing an object of class {@code cls}.
   */
  <K extends Activatable> K getReference(Class<K> cls);

  int getPort();
}
