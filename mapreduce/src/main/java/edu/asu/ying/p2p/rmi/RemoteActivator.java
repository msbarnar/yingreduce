package edu.asu.ying.p2p.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import javax.annotation.Nullable;

public interface RemoteActivator extends Remote, Serializable {

  <T extends Remote> T getReference(final Class<T> type,
                                    final @Nullable Map<String, String> properties)
      throws RemoteException;
}
