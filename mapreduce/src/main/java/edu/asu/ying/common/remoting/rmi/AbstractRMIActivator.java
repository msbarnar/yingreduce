package edu.asu.ying.common.remoting.rmi;

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
 * Controls creation and lifetime management for server-side object instances available for
 * accession by remote nodes.
 */
public abstract class AbstractRMIActivator implements Activator {

  // The port used to export all RMI instances.
  protected transient int rmiPort = 0;
  protected final transient Object portLock = new Object();

  // Activation of other classes is controlled via bindings
  // Interface -> proxy
  protected final transient Map<Class<? extends Activatable>, Remote> bindings = new HashMap<>();

  protected AbstractRMIActivator(int port) {
    this.rmiPort = port;

  }

  @Override
  public <R extends Activatable, T extends R> R bind(Class<R> cls, T target)
      throws ExportException {

    T instance;
    try {
      instance = export(target);
    } catch (RemoteException e) {
      throw new ExportException("Exception exporting RMI proxy", e);
    }
    bindings.put(cls, instance);
    return instance;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R extends Activatable> R getReference(Class<R> cls) throws ClassNotExportedException {

    R reference = (R) bindings.get(cls);
    if (reference == null) {
      throw new ClassNotExportedException();
    }
    return reference;
  }

  @SuppressWarnings("unchecked")
  protected <R extends Activatable> R export(R obj) throws ExportException {
    try {
      return (R) UnicastRemoteObject.exportObject(obj, getPort());
    } catch (RemoteException e) {
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
          rmiPort = getDefaultPort();
        }
      }
    }
    return rmiPort;
  }

  /**
   * Selects a random port and specifies it as the sole port for RMI.
   */
  protected int getDefaultPort() {
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
