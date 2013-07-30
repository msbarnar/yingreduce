package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import javax.annotation.Nullable;

public interface Activator extends Remote {

  <T extends Remote> T export(final T instance, final Map<String, String> properties)
      throws RemoteException;

  <T extends Remote> T getReference(final Class<T> type,
                                    final @Nullable Map<String, String> properties)
      throws RemoteException;
}
