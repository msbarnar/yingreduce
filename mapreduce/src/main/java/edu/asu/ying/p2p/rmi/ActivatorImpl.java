package edu.asu.ying.p2p.rmi;

import com.javafx.tools.doclets.internal.toolkit.taglets.InheritDocTaglet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * Controls creation and lifetime management for server-side object instances available for
 * accession by remote nodes.
 */
public final class ActivatorImpl implements RemoteActivator, ServerActivator {

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
    final Binder binder = new BinderImpl<>(type);
    this.bindings.put(type, binder);
    return binder;
  }

  /**
   * @inheritDoc
   */
  @Override
  public final RemoteActivator export() {
    if (this.exportedInstance == null) {
      synchronized (this.exportedInstanceLock) {
        if (this.exportedInstance == null) {
          try {
            this.exportedInstance = (RemoteActivator) UnicastRemoteObject.exportObject(this, 15999);
          } catch (final RemoteException e) {
            // TODO: Logging
            e.printStackTrace();
          }
        }
      }
    }
    return this.exportedInstance;
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
}
