package edu.asu.ying.p2p.rmi;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @inheritDoc
 */
public final class BinderImpl<K extends Activatable>
    implements Activator.Binder<K> {

  /**
   * {@code InstanceBinding} binds a class to a specific proxyInstance of that class.
   */
  @SuppressWarnings("unchecked")
  private final class InstanceBinding<K extends Activatable>
      implements Activator.Binding<K> {

    private final Class<K> bindee;
    private final K instance;

    private InstanceBinding(final Class<K> bindee, final K instance,
                            final Activator activator) {
      this.bindee = bindee;
      K proxyInstance = null;
      try {
        // TODO: Port in configuration
        proxyInstance = (K) UnicastRemoteObject.exportObject(instance,
                                                             activator.getPort());
      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
      this.instance = proxyInstance;
    }

    public final K getReference() {
      return this.instance;
    }
  }

  @SuppressWarnings("unchecked")
  private final class LazyInstanceBinding<K extends Activatable>
      implements Activator.Binding<K> {

    private final Activator activator;
    private K proxyInstance;
    // Keep the target in scope so the connection stays open
    private K targetInstance;

    private LazyInstanceBinding(final Activator activator) {
      this.activator = activator;
    }

    private void set(final K instance) {
      // For some reason if we don't keep it in scope here, the listening socket closes as soon
      // as set() leaves scope
      this.targetInstance = instance;
      K proxyInstance = null;
      try {
        // TODO: Port in configuration
        proxyInstance = (K) UnicastRemoteObject.exportObject(this.targetInstance,
                                                             this.activator.getPort());
      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
      this.proxyInstance = proxyInstance;
    }

    public final K getReference() {
      return this.proxyInstance;
    }
  }

  private final class ViaBinderImpl<T extends K, V extends T>
      implements Activator.ViaBinder<T> {

    private final Class<V> proxyClass;
    private LazyInstanceBinding<T> binding;

    private ViaBinderImpl(final LazyInstanceBinding<T> binding,
                          final Class<V> proxyClass) {
      this.proxyClass = proxyClass;
      this.binding = binding;
    }

    @Override
    public T toInstance(final Object instance) throws ExportException {
      try {
        this.binding.set(ConstructorUtils.invokeConstructor(this.proxyClass, instance));

      } catch (final NoSuchMethodException e) {
        throw new ExportException("Proxy class does not have a constructor which takes this class "
                                  + "as the sole argument.", e);
      } catch (final IllegalAccessException e) {
        throw new ExportException("Proxy class constructor is not accessible.", e);
      } catch (final InstantiationException e) {
        throw new ExportException("Proxy class can not be instantiated.", e);
      } catch (final InvocationTargetException e) {
        throw new ExportException("Proxy class constructor threw an exception.", e);
      }

      return this.binding.getReference();
    }
  }

  private final Activator activator;
  private final Class<K> boundClass;
  private Activator.Binding<K> binding;

  public BinderImpl(final Class<K> boundClass, final Activator activator) {
    this.boundClass = boundClass;
    this.activator = activator;
  }

  public K toInstance(final K instance) {
    this.binding = new InstanceBinding<>(this.boundClass, instance, this.activator);
    return this.binding.getReference();
  }

  @Override
  public <V extends K> Activator.ViaBinder<K> to(Class<V> proxyClass) {
    this.binding = new LazyInstanceBinding<K>(this.activator);
    return new ViaBinderImpl<>((LazyInstanceBinding<K>) this.binding,
                               proxyClass);
  }

  public Activator.Binding getBinding() {
    return this.binding;
  }
}
