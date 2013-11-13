package edu.asu.ying.common.remoting.rmi;

import com.google.inject.Inject;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.ClassNotExportedException;


/**
 * The {@code RMIActivator} binds classes to {@link Remote} proxy instances, maintaining strong
 * references to both the proxy and the instance it references to control the lifetime of exported
 * objects. </p> All objects are exported to the same port, which is injected with {@link RMIPort}.
 * The port is random by default.
 */
public class RMIActivator implements Activator {

  // The port used to export all RMI instances.
  protected transient int rmiPort = 0;
  protected final transient Object portLock = new Object();

  // Activation of other classes is controlled via bindings
  // Interface -> proxy
  protected final transient Map<Class<? extends Activatable>, Binding<?>> bindings
      = new HashMap<>();

  @Inject
  protected RMIActivator(@RMIPort int port) {
    this.rmiPort = port;
  }

  @Override
  public <R extends Activatable, T extends R> R bind(Class<R> cls, T target)
      throws ExportException {

    T proxy;
    try {
      proxy = export(target);
    } catch (RemoteException e) {
      throw new ExportException("Exception exporting RMI proxy", e);
    }
    bindings.put(cls, new Binding<>(target, proxy));
    return proxy;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R extends Activatable> R getReference(Class<R> cls) throws ClassNotExportedException {

    Binding<?> binding = bindings.get(cls);
    if (binding == null || bindings.get(cls).getProxy() == null) {
      throw new ClassNotExportedException();
    }

    return (R) binding.getProxy();
  }

  @SuppressWarnings("unchecked")
  protected <R extends Activatable> R export(R obj) throws ExportException {
    try {
      return (R) UnicastRemoteObject.exportObject(obj, getPort());
    } catch (RemoteException e) {
      if (e.getMessage().startsWith("Port already in use")) {
        // This is fixable; pick a new port and try again
        synchronized (portLock) {
          rmiPort = getRandomPort();
        }
        return export(obj);
      }
      throw new ExportException("Exception exporting RMI proxy", e);
    }
  }

  /**
   * Use a single port per node for all RMI proxies.
   */
  protected int getPort() {
    if (rmiPort <= 0) {
      synchronized (portLock) {
        if (rmiPort <= 0) {
          rmiPort = getRandomPort();
        }
      }
    }
    return rmiPort;
  }

  /**
   * Selects a random port and specifies it as the sole port for RMI.
   */
  protected int getRandomPort() {
    ServerSocket sock;
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

  /**
   * Maintains strong references to the proxy and target classes so the proxies aren't invalidated.
   */
  private final class Binding<T> {

    private final T target;
    private final Remote proxy;

    private Binding(T target, Remote proxy) {
      this.target = target;
      this.proxy = proxy;
    }

    Remote getProxy() {
      return proxy;
    }
  }
}
