package edu.asu.ying.p2p.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * Controls creation and lifetime management for server-side object instances available for
 * accession by remote nodes.
 */
public final class RMIActivatorImpl implements RMIActivator {

  // The port used to export all RMI instances.
  private int rmiPort = 0;
  private final Object portLock = new Object();

  // Activation of other classes is controlled via bindings
  // i.e. Class/Interface -> Subclass or Instance
  private final Map<Class<? extends Remote>, Binder<? extends Remote>> bindings = new HashMap<>();

  public RMIActivatorImpl() {
  }

  /**
   * @inheritDoc
   */
  @Override
  @SuppressWarnings("unchecked")
  public final <TBound extends Remote> Binder<TBound> bind(final Class<TBound> type) {
    final Binder binder = new BinderImpl<>(type, this);
    this.bindings.put(type, binder);
    return binder;
  }

  /**
   * @inheritDoc
   */
  @Override
  @SuppressWarnings("unchecked")
  public final <TBound extends Remote> TBound getReference(final Class<TBound> type) {

    final Binder<?> binder = this.bindings.get(type);
    if (binder == null)
      return null;

    final Binding<?> binding = binder.getBinding();
    if (binding == null)
      return null;

    return (TBound) binding.getReference();
  }

  @Override
  public final int getPort() {
    if (this.rmiPort == 0) {
      synchronized(this.portLock) {
        if (this.rmiPort == 0) {
          this.rmiPort = this.allocatePort();
        }
      }
    }
    return this.rmiPort;
  }

  /**
   * Selects a random port and specifies it as the sole port for RMI.
   */
  private int allocatePort() {
    ServerSocket sock = null;
    int port;
    try {
      sock = new ServerSocket(0);
      port = sock.getLocalPort();
      sock.close();
    } catch (final IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return port;
  }
}
