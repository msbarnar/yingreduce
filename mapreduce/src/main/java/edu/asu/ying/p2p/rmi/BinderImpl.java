package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @inheritDoc
 */
public final class BinderImpl<TBound extends Remote>
    implements ServerActivator.Binder<TBound> {

  /**
   * {@code ClassBinding} binds a class to another class with an activation mode controlling the
   * instantiation of the bound class.
   */
  private final class ClassBinding<TBound extends Remote, TBindee extends TBound>
      implements ServerActivator.Binding<TBound> {

    /**
     * Provides instances of {@code TBound} according to the
     * {@link edu.asu.ying.p2p.rmi.ServerActivator.ActivationMode}.
     */
    private abstract class InstanceFactory<TBindee> {
      protected final Class<TBindee> type;
      protected InstanceFactory(final Class<TBindee> type) {
        this.type = type;
      }
      abstract TBindee get();
    }

    /**
     * Provides a new instance of {@code TBindee} per call.
     */
    private final class SingleCallFactory<TBindee extends Remote> extends InstanceFactory<TBindee> {
      private SingleCallFactory(final Class<TBindee> type) {
        super(type);
      }
      final TBindee get() {
        try {
          return (TBindee) UnicastRemoteObject.exportObject(this.type.newInstance(),
                                                            15999);

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
    private final class SingletonFactory<TBindee extends Remote> extends InstanceFactory<TBindee> {
      private TBindee instance;
      private final Object instanceLock = new Object();

      private SingletonFactory(final Class<TBindee> type) {
        super(type);
      }
      final TBindee get() {
        try {
          if (this.instance == null) {
            synchronized (this.instanceLock) {
              if (this.instance == null) {
                // TODO: Port in configuration
                this.instance = (TBindee) UnicastRemoteObject.exportObject(this.type.newInstance(),
                                                                           15999);
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
                        final ServerActivator.ActivationMode mode) {
      this.boundType = boundType;
      this.bindee = bindee;

      switch (mode) {
        case SingleCall:
          this.factory = new SingleCallFactory<>(bindee);
          break;
        case Singleton:
          this.factory = new SingletonFactory<>(bindee);
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
  private final class InstanceBinding<TBound extends Remote>
      implements ServerActivator.Binding<TBound> {

    private final Class<TBound> bindee;
    private final TBound instance;

    private InstanceBinding(final Class<TBound> bindee, final TBound instance) {
      this.bindee = bindee;
      TBound proxyInstance = null;
      try {
        // TODO: Port in configuration
        proxyInstance = (TBound) UnicastRemoteObject.exportObject(instance, 15999);
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

  private final Class<TBound> boundClass;
  private ServerActivator.Binding<TBound> binding;

  public BinderImpl(final Class<TBound> boundClass) {
    this.boundClass = boundClass;
  }

  @Override
  public <TBindee extends TBound> void
  to(Class<TBindee> type, ServerActivator.ActivationMode mode) {
    this.binding = new ClassBinding<>(this.boundClass, type, mode);
  }

  @Override
  public void toInstance(TBound instance) {
    this.binding = new InstanceBinding<>(this.boundClass, instance);
  }

  @Override
  public ServerActivator.Binding getBinding() {
    return this.binding;
  }
}
