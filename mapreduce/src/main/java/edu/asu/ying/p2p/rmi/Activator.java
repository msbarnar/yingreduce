package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.server.ExportException;

/**
 *
 */
public interface Activator extends Activatable {

  /**
   * Creates a binding for the class {@code cls}. </p> The binding is not active until it is
   * assigned to a target.
   */
  <R extends Activatable, T extends R> R bind(Class<R> cls, T target) throws ExportException;

  /**
   * Gets a {@link Remote} proxy referencing an object of class {@code cls}.
   */
  <R extends Activatable> R getReference(Class<R> cls) throws ReferenceNotExportedException;

  int getPort();
}
