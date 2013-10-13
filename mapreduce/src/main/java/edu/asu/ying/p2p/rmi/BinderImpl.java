package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @inheritDoc
 */
final class BinderImpl<K extends Activatable> implements Binder<K> {

  private final Activator activator;
  private Binding<K> binding;

  public BinderImpl(Activator activator) {
    this.activator = activator;
  }

  @Override
  public <T extends K> K toInstance(T instance) {
    this.binding = new InstanceBinding<K>(instance, this.activator);
    return this.binding.getReference();
  }

  @Override
  public <T> WrapperBinder<T, K> to(T targetInstance) {
    WrapperBinder<T, K> binder = new WrapperBinderImpl<T, K>(targetInstance, this.activator);
    this.binding = binder.getBinding();
    return binder;
  }

  public Binding<K> getBinding() {
    return this.binding;
  }

  /**
   * {@code InstanceBinding} binds a class to a specific instance of that class.
   */
  @SuppressWarnings("unchecked")
  private final class InstanceBinding<K extends Activatable> implements Binding<K> {

    private final K proxy;

    private InstanceBinding(K instance, Activator activator) {

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

  private final class WrapperBinderImpl<T, K extends Activatable> implements WrapperBinder<T, K> {

    private final Activator activator;
    private final T targetInstance;
    private LazyBinding<K> binding;

    private WrapperBinderImpl(T targetInstance, Activator activator) {
      this.activator = activator;
      this.targetInstance = targetInstance;
      this.binding = new LazyBinding<>();
    }

    @Override
    public <W extends WrapperFactory<T, K>> K wrappedBy(W wrapperFactory) throws ExportException {
      this.binding.set(new InstanceBinding<>(wrapperFactory.create(this.targetInstance,
                                                                   this.activator),
                                             this.activator));
      return this.binding.getReference();
    }

    @Override
    public Binding<K> getBinding() {
      return this.binding;
    }
  }

  private final class LazyBinding<K extends Activatable> implements Binding<K> {

    private Binding<K> binding;

    void set(Binding<K> binding) {
      this.binding = binding;
    }

    public K getReference() {
      return this.binding.getReference();
    }
  }
}