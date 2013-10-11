package edu.asu.ying.p2p.rmi;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
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

  private final class ViaBinderImpl<K extends Activatable, T> implements ViaBinder<K> {

    private final Activator activator;
    private final T targetInstance;
    private InstanceBinding<K> binding;

    private ViaBinderImpl(final T targetInstance, final Activator activator) {
      this.activator = activator;
      this.targetInstance = targetInstance;
    }

    @Override
    public <W extends K> K via(final Class<W> wrapper) throws ExportException {
      try {
        this.binding = new InstanceBinding<K>(
            ConstructorUtils.invokeConstructor(wrapper, this.targetInstance),
            this.activator);

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
  public <T> ViaBinder<K> to(final T targetInstance) {
    return new ViaBinderImpl<K, T>(targetInstance, this.activator);
  }

  public Binding<K> getBinding() {
    return this.binding;
  }
}