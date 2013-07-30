package edu.asu.ying.p2p.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import javax.annotation.Nullable;

public interface RemoteActivator extends Remote, Serializable {

  /**
   * Returns an instance of a {@link Remote} proxy to an object of class {@code type}.
   * </p>
   * The instantiation and lifetime of the concrete object is determined at the time {@code type} is
   * bound on the server. The proxy returned may reference a singleton instance or may reference
   * a different instance each time {@link ActivatorImpl#getReference} is called.
   * @param type the class of the proxy to return.
   * @param properties instantiation properties, where appropriate.
   * @param <TBound> the type of the proxy.
   */
  <TBound extends Remote> TBound getReference(final Class<TBound> type,
                                    final @Nullable Map<String, String> properties)
      throws RemoteException;
}
