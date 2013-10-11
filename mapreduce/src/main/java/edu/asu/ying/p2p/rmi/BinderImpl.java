package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @inheritDoc
 */
final class BinderImpl<K extends Activatable> implements Binder<K> {

  /**
   * {@code InstanceBinding} binds a class to a specific instance of that class.
   */
  @SuppressWarnings("unchecked")
  private final class InstanceBinding<K extends Activatable> implements Binding<K> {

    private final K proxy;

    private InstanceBinding(final K instance, final Activator activator) {

      K proxy = null;
      try {
        // TODO: Port in configuration
        proxy = (K) UnicastRemoteObject.exportObject(instance, activator.getPort());
      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
      this.proxy = proxy;
    }

    public K getReference() {
      return this.proxy;
    }
  }

  private final class WrapperBinderImpl<K extends Activatable, T> implements WrapperBinder<T, K> {

    private final Activator activator;
    private final T targetInstance;
    private InstanceBinding<K> binding;

    private WrapperBinderImpl(final T targetInstance, final Activator activator) {
      this.activator = activator;
      this.targetInstance = targetInstance;
    }

    @Override
    public K wrappedBy(final WrapperFactory<T, K> wrapper) throws ExportException {
      this.binding = new InstanceBinding<>(wrapper.createWrapper(this.targetInstance),
                                           this.activator);

      return this.binding.getReference();
    }
  }

  private final Activator activator;
  private Binding<K> binding;

  public BinderImpl(final Activator activator) {
    this.activator = activator;
  }

  @Override
  public <T extends K> K toInstance(final T instance) {
    this.binding = new InstanceBinding<K>(instance, this.activator);
    return this.binding.getReference();
  }

  @Override
  public <T> WrapperBinder<T, K> to(final T targetInstance) {
    return new WrapperBinderImpl<K, T>(targetInstance, this.activator);
  }

  public Binding<K> getBinding() {
    return this.binding;
  }
}