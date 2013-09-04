package edu.asu.ying.p2p.rmi;

import com.javafx.tools.doclets.internal.toolkit.taglets.InheritDocTaglet;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * Controls creation and lifetime management for server-side object instances available for
 * accession by remote nodes.
 */
public final class ActivatorImpl implements RemoteActivator, ServerActivator {

  // The port used to export all RMI instances.
  private int rmiPort = 0;

  // (singleton) Proxy instance accessible via RMI
  private RemoteActivator exportedInstance = null;
  private final Object exportedInstanceLock = new Object();

  // Activation of other classes is controlled via bindings
  // i.e. Class/Interface -> Subclass or Instance
  private final Map<Class<? extends Remote>, Binder<? extends Remote>> bindings = new HashMap<>();

  public ActivatorImpl() {
  }

  /**
   * @inheritDoc
   */
  @Override
  public final <TBound extends Remote> Binder bind(final Class<TBound> type) {
    final Binder binder = new BinderImpl<>(type, this);
    this.bindings.put(type, binder);
    return binder;
  }

  /**
   * @inheritDoc
   */
  @Override
  public final RemoteActivator export() {
    final Binder binder = new BinderImpl<>(RemoteActivator.class, this);
    binder.to(ActivatorImpl.class,
               ActivationMode.Singleton);
    return (RemoteActivator) binder.getBinding().getReference();
  }

  /**
   * @inheritDoc
   */
  @Override
  public final <TBound extends Remote> TBound getReference(final Class<TBound> type,
                                           final @Nullable Map<String, String> properties)
    throws RemoteException {

    final Binder<?> binder = this.bindings.get(type);
    if (binder == null)
      return null;

    final Binding<?> binding = binder.getBinding();
    if (binding == null)
      return null;

    return (TBound) binding.getReference();
  }

  /**
   * Selects a random port and specifies it as the sole port for RMI.
   */
  private int allocatePort() {
    ServerSocket sock = null;
    try {
      sock = new ServerSocket(0);
      this.rmiPort = sock.getLocalPort();
      sock.close();
    } catch (final IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return this.rmiPort;
  }
}
