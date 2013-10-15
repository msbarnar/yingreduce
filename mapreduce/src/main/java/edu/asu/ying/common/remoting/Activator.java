package edu.asu.ying.common.remoting;

import java.rmi.server.ExportException;

/**
 * The {@code Activator} manages the binding of {@link Activatable} instances to the network
 * interface. Instances bound will be made accessible publically and a strong reference will be held
 * for the lifetime of the {@code Activator}.
 */
public interface Activator extends Activatable {

  /**
   * Binds the class {@code cls} to an exported proxy for {@code instance}. Requests for {@code cls}
   * will be fulfilled with the proxy, not {@code instance} itself.
   */
  <R extends Activatable, I extends R> R bind(Class<R> cls, I instance) throws ExportException;

  /**
   * Gets an instance of the proxy bound to {@code cls}, if any.
   */
  <R extends Activatable> R getReference(Class<R> cls) throws ClassNotExportedException;
}
