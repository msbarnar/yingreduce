package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
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
     * Provides instances of {@code TBound} according to the
     * {@link RMIActivator.ActivationMode}.
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

        } catch (final InstantiationException | IllegalAccessException | RemoteException e) {
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
                                                                           this.activator.getPort());
              }
            }
          }
          return this.instance;

        } catch (final InstantiationException | IllegalAccessException | RemoteException e) {
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
          this.factory = new SingleCallFactory<>(bindee, activator);
          break;
        case Singleton:
          this.factory = new SingletonFactory<>(bindee, activator);
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    @Override
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

    @Override
    public final TBound getReference() {
      return this.instance;
    }
  }

  private final RMIActivator activator;
  private final Class<TBound> boundClass;
  private RMIActivator.Binding<TBound> binding;

  public BinderImpl(final Class<TBound> boundClass, final RMIActivator activator) {
    this.boundClass = boundClass;
    this.activator = activator;
  }

  @Override
  public <TBindee extends TBound> void
  to(Class<TBindee> type, RMIActivator.ActivationMode mode) {
    this.binding = new ClassBinding<>(this.boundClass, type, mode, this.activator);
  }

  @Override
  public void toInstance(TBound instance) {
    this.binding = new InstanceBinding<>(this.boundClass, instance, this.activator);
  }

  @Override
  public RMIActivator.Binding getBinding() {
    return this.binding;
  }
}
