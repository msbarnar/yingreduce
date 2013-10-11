package edu.asu.ying.p2p.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;


/**
 * Controls creation and lifetime management for server-side object instances available for
 * accession by remote nodes.
 */
public final class ActivatorImpl implements Activator {

  // The port used to export all RMI instances.
  private int rmiPort = 0;
  private final Object portLock = new Object();

  // Activation of other classes is controlled via bindings
  // i.e. Class/Interface -> Subclass or Instance
  private final Map<Class<?>, Binder<? extends Activatable>> bindings = new HashMap<>();

  public ActivatorImpl() {
  }

  /**
   * @inheritDoc
   */
  @SuppressWarnings("unchecked")
  public <K extends Activatable> Binder<K> bind(final Class<K> boundClass) {
    final Binder binder = new BinderImpl<>(this);
    this.bindings.put(boundClass, binder);
    return binder;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K extends Activatable> K getReference(Class<K> cls) {
    final Binder<?> binder = this.bindings.get(cls);
    if (binder == null) {
      return null;
    }

    final Binding<?> binding = binder.getBinding();
    if (binding == null) {
      return null;
    }

    return (K) binding.getReference();
  }

  /**
   * Use a single port per node for all RMI proxies.
   */
  public final int getPort() {
    if (this.rmiPort == 0) {
      synchronized (this.portLock) {
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
