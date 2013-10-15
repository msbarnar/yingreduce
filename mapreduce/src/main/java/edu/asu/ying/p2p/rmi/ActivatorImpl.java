package edu.asu.ying.p2p.rmi;

import com.google.inject.Inject;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.p2p.Channel;
import edu.asu.ying.p2p.message.Message;
import edu.asu.ying.p2p.message.MessageHandler;
import edu.asu.ying.p2p.message.ResponseMessage;


/**
 * Controls creation and lifetime management for server-side object instances available for
 * accession by remote nodes.
 */
public final class ActivatorImpl implements Activator, MessageHandler {

  // The port used to export all RMI instances.
  private transient int rmiPort = 0;
  private final transient Object portLock = new Object();

  private final transient RemotePeer proxyInstance;

  // Activation of other classes is controlled via bindings
  // Interface -> proxy
  private final transient Map<Class<? extends Activatable>, Remote> bindings = new HashMap<>();

  @Inject
  private ActivatorImpl(@RMIPort int port, Channel channel) {
    this.rmiPort = port;
    try {
      this.proxyInstance = bind(RemotePeer.class, new RemotePeerWrapper(this));
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }
    channel.registerMessageHandler(this, "p2p.activator");
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
  public <R extends Activatable> R getReference(Class<R> cls) throws ReferenceNotExportedException {

    R reference = (R) bindings.get(cls);
    if (reference == null) {
      throw new ReferenceNotExportedException();
    }
    return reference;
  }

  @SuppressWarnings("unchecked")
  private <R extends Activatable> R export(R obj) throws ExportException {
    try {
      return (R) UnicastRemoteObject.exportObject(obj, getPort());
    } catch (RemoteException e) {
      throw new ExportException("Exception exporting RMI proxy", e);
    }
  }

  /**
   * Use a single port per node for all RMI proxies.
   */
  public int getPort() {
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
  private int getDefaultPort() {
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

  @Override
  public void onIncomingMessage(Message message) {
    // TODO: Logging
  }

  @Override
  public Message onIncomingRequest(Message request) {
    final ResponseMessage response = ResponseMessage.inResponseTo(request);
    response.setContent(proxyInstance);
    return response;
  }
}
