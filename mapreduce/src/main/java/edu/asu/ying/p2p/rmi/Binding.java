package edu.asu.ying.p2p.rmi;

/**
 * A {@code Binding} fulfills a request for an object of type {@code K} by providing an instance of
 * {@code K} or one of its subclasses.
 */
interface Binding<K extends Activatable> {

  /**
   * Provides a fully exported {@link java.rmi.Remote} proxy of type {@code K}.
   */
  K getReference();
}
