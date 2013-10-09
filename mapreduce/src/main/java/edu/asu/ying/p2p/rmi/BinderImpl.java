package edu.asu.ying.p2p.rmi;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @inheritDoc
 */
public final class BinderImpl<TBound extends Remote>
    implements RMIActivator.Binder<TBound> {

  /**
   * {@code ClassBinding} binds a class to another class with an activation mode controlling the
   * instantiation of the bound class.
   */
  private final class ClassBinding<TBound extends Remote, TBindee extends TBound>
      implements RMIActivator.Binding<TBound> {

    /**
     * Provides instances of {@code TBound} according to the {@link RMIActivator.ActivationMode}.
     */
    private abstract class InstanceFactory<TBindee> {

      protected final RMIActivator activator;
      protected final Class<TBindee> type;

      protected InstanceFactory(final Class<TBindee> type, final RMIActivator activator) {
        this.type = type;
        this.activator = activator;
      }

      abstract TBindee get();
    }

    /**
     * Provides a new instance of {@code TBindee} per call.
     */
    @SuppressWarnings("unchecked")
    private final class SingleCallFactory<TBindee extends Remote> extends InstanceFactory<TBindee> {

      private SingleCallFactory(final Class<TBindee> type, final RMIActivator activator) {
        super(type, activator);
      }

      final TBindee get() {
        try {
          return (TBindee) UnicastRemoteObject.exportObject(this.type.newInstance(),
                                                            this.activator.getPort());

        } catch (final Exception e) {
          // TODO: Logging
          e.printStackTrace();
          return null;
        }
      }
    }

    /**
     * Provides a singleton instance of {@code TBindee}.
     */
    @SuppressWarnings("unchecked")
    private final class SingletonFactory<TBindee extends Remote> extends InstanceFactory<TBindee> {

      private TBindee instance;
      private final Object instanceLock = new Object();

      private SingletonFactory(final Class<TBindee> type, final RMIActivator activator) {
        super(type, activator);
      }

      final TBindee get() {
        try {
          if (this.instance == null) {
            synchronized (this.instanceLock) {
              if (this.instance == null) {
                // TODO: Port in configuration
                this.instance = (TBindee) UnicastRemoteObject.exportObject(this.type.newInstance(),
                                                                           this.activator
                                                                               .getPort());
              }
            }
          }
          return this.instance;

        } catch (final Exception e) {
          // TODO: Logging
          e.printStackTrace();
          return null;
        }
      }
    }

    // The type BOUND
    private final Class<TBound> boundType;
    // The type TO WHICH IT IS BOUND
    private final Class<TBindee> bindee;
    // Provides fully exported proxies according to activation mode
    private final InstanceFactory<TBindee> factory;

    /**
     * Binds {@code boundType} to {@code bindee} given the activation {@code mode}.
     */
    public ClassBinding(final Class<TBound> boundType, final Class<TBindee> bindee,
                        final RMIActivator.ActivationMode mode,
                        final RMIActivator activator) {
      this.boundType = boundType;
      this.bindee = bindee;

      switch (mode) {
        case SingleCall:
          this.factory = new SingleCallFactory<TBindee>(bindee, activator);
          break;
        case Singleton:
          this.factory = new SingletonFactory<TBindee>(bindee, activator);
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    public final TBound getReference() {
      return this.factory.get();
    }
  }

  /**
   * {@code InstanceBinding} binds a class to a specific instance of that class.
   */
  @SuppressWarnings("unchecked")
  private final class InstanceBinding<TBound extends Remote>
      implements RMIActivator.Binding<TBound> {

    private final Class<TBound> bindee;
    private final TBound instance;

    private InstanceBinding(final Class<TBound> bindee, final TBound instance,
                            final RMIActivator activator) {
      this.bindee = bindee;
      TBound proxyInstance = null;
      try {
        // TODO: Port in configuration
        proxyInstance = (TBound) UnicastRemoteObject.exportObject(instance,
                                                                  activator.getPort());
      } catch (final RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
      this.instance = proxyInstance;
    }

    public final TBound getReference() {
      return this.instance;
    }
  }

  private final class ViaBinderImpl<T extends TBound, TBindee extends T>
      implements RMIActivator.ViaBinder<T> {

    private final Class<T> boundClass;
    private final RMIActivator activator;
    private final Class<TBindee> proxyClass;
    private T proxyTarget;
    private RMIActivator.Binding<T> binding;

    private ViaBinderImpl(final Class<T> boundClass, final RMIActivator activator,
                          final Class<TBindee> proxyClass) {
      this.boundClass = boundClass;
      this.activator = activator;
      this.proxyClass = proxyClass;
    }

    @Override
    public T toInstance(final Object instance) throws ExportException {
      try {
        this.proxyTarget = ConstructorUtils.invokeConstructor(this.proxyClass, instance);

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

      this.binding = new InstanceBinding<>(boundClass, this.proxyTarget, this.activator);
      return this.binding.getReference();
    }
  }

  private final RMIActivator activator;
  private final Class<TBound> boundClass;
  private RMIActivator.Binding<TBound> binding;

  public BinderImpl(final Class<TBound> boundClass, final RMIActivator activator) {
    this.boundClass = boundClass;
    this.activator = activator;
  }

  public <TBindee extends TBound> TBound
  to(final Class<TBindee> type, final RMIActivator.ActivationMode mode) {
    this.binding = new ClassBinding<>(this.boundClass, type, mode, this.activator);
    return this.binding.getReference();
  }


  public TBound toInstance(final TBound instance) {
    this.binding = new InstanceBinding<>(this.boundClass, instance, this.activator);
    return this.binding.getReference();
  }

  @Override
  public <TBindee extends TBound> RMIActivator.ViaBinder<TBound> via(Class<TBindee> proxyClass) {
    return new ViaBinderImpl<>(this.boundClass, this.activator, proxyClass);
  }

  public RMIActivator.Binding getBinding() {
    return this.binding;
  }
}
