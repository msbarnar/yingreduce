package edu.asu.ying.p2p.rmi;

import com.google.inject.Inject;

import java.io.IOException;
import java.net.ServerSocket;
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
  private final transient Map<Class<? extends Activatable>, Binding<?>> bindings = new HashMap<>();

  @Inject
  private ActivatorImpl(@RMIPort int port, Channel channel) {
    this.rmiPort = port;
    try {
      this.proxyInstance = bind(RemotePeer.class, this, new RemotePeerWrapper(this));
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
    bindings.put(cls, new Binding<>(instance));
    return instance;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R extends Activatable, T, W extends Wrapper<R, ? super T>> R bind(Class<R> cls,
                                                                            T target,
                                                                            W wrapper)
      throws ExportException {
    R instance;
    try {
      wrapper.wrap(target);
      instance = (R) export(wrapper);
    } catch (RemoteException | ClassCastException e) {
      throw new ExportException("Exception exporting RMI proxy", e);
    }
    bindings.put(cls, new WrapperBinding<>(instance, wrapper));
    return instance;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R extends Activatable> R getReference(Class<R> cls) throws ReferenceNotExportedException {

    Binding<?> binding = bindings.get(cls);
    if (binding == null || binding.getProxy() == null) {
      throw new ReferenceNotExportedException();
    }
    return (R) binding.getProxy();
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

  private class Binding<R> {

    private final R proxyInstance;

    protected Binding(R proxyInstance) {
      this.proxyInstance = proxyInstance;
    }

    private R getProxy() {
      return proxyInstance;
    }
  }

  private final class WrapperBinding<R, W> extends Binding<R> {

    private final W wrapper;

    private WrapperBinding(R proxyInstance, W wrapper) {
      super(proxyInstance);
      this.wrapper = wrapper;
    }
  }
}
