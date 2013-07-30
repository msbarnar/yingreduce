package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Binds the type {@code T} to a child or instance of that type.
 * </p>
 * When requests to the {@link RemoteActivator} for that type are made, references will be provided
 * according to the binding.
 */
public final class BinderImpl<TBound extends Remote>
    implements ServerActivator.Binder<TBound> {


  /**
   * {@code ClassBinding} binds a class to another class with an instantiation mode.
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
    private final class SingleCallFactory<TBindee extends Remote> extends InstanceFactory<TBindee> {
      private SingleCallFactory(final Class<TBindee> type) {
        super(type);
      }
      final TBindee get() {
        try {
          return this.type.newInstance();

        } catch (final InstantiationException | IllegalAccessException e) {
          // TODO: Logging
          e.printStackTrace();
          return null;
        }
      }
    }
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

    private final Class<TBound> bindee;
    private final Class<TBindee> boundType;
    private final InstanceFactory<TBindee> factory;
    private TBindee instance;
    private final Object instanceLock = new Object();

    public ClassBinding(final Class<TBound> bindee, final Class<TBindee> type,
                        final ServerActivator.ActivationMode mode) {
      this.bindee = bindee;
      this.boundType = type;

      switch (mode) {
        case SingleCall:
          this.factory = new SingleCallFactory<>(type);
          break;
        case Singleton:
          this.factory = new SingletonFactory<>(type);
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
   * {@code InstanceBinding} binds a class to a specific instance.
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
