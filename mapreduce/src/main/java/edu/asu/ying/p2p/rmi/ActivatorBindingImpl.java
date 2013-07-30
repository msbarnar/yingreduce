package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;

/**
 * Binds the type {@code T} to a child or instance of that type.
 * </p>
 * When requests to the {@link Activator} for that type are made, references will be provided
 * according to the binding.
 */
public final class ActivatorBindingImpl<TBindee extends Remote>
    implements ServerActivator.ActivatorBinding<TBindee> {

  /**
   * Base class for the different kinds of bindings (to-class or to-instance).
   */
  private abstract class Binding<TBindee extends Remote> {
    protected final Class<TBindee> bindee;

    protected Binding(final Class<TBindee> bindee) {
      this.bindee = bindee;
    }

    abstract TBindee getReference() throws InstantiationException, IllegalAccessException ;
  }

  /**
   * {@code ClassBinding} binds a class to another class with an instantiation mode.
   */
  private final class ClassBinding<TBindee extends Remote, TBound extends TBindee>
      extends Binding<TBindee> {

    /**
     * Provides instances of {@code TBound} according to the
     * {@link edu.asu.ying.p2p.rmi.ServerActivator.ActivationMode}.
     */
    private abstract class InstanceFactory<TBound> {
      protected final Class<TBound> type;
      protected InstanceFactory(final Class<TBound> type) {
        this.type = type;
      }
      abstract TBound get();
    }
    private final class SingleCallFactory<TBound> extends InstanceFactory<TBound> {
      private SingleCallFactory(final Class<TBound> type) {
        super(type);
      }
      final TBound get() {
        try {
          return this.type.newInstance();

        } catch (final InstantiationException | IllegalAccessException e) {
          // TODO: Logging
          e.printStackTrace();
          return null;
        }
      }
    }
    private final class SingletonFactory<TBound> extends InstanceFactory<TBound> {
      private TBound instance;
      private final Object instanceLock = new Object();

      private SingletonFactory(final Class<TBound> type) {
        super(type);
      }
      final TBound get() {
        try {
          if (this.instance == null) {
            synchronized (this.instanceLock) {
              if (this.instance == null) {
                this.instance = this.type.newInstance();
              }
            }
          }
          return this.instance;

        } catch (final InstantiationException | IllegalAccessException e) {
          // TODO: Logging
          e.printStackTrace();
          return null;
        }
      }
    }

    private final Class<TBound> boundType;
    private final InstanceFactory<TBound> factory;
    private TBound instance;
    private final Object instanceLock = new Object();

    public ClassBinding(final Class<TBindee> bindee, final Class<TBound> type,
                        final ServerActivator.ActivationMode mode) {
      super(bindee);
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
    public final TBindee getReference() throws InstantiationException, IllegalAccessException {
      return this.factory.get();
    }
  }

  /**
   * {@code InstanceBinding} binds a class to a specific instance.
   */
  private final class InstanceBinding<TBindee extends Remote> extends Binding<TBindee> {
    private final TBindee instance;

    private InstanceBinding(final Class<TBindee> bindee, final TBindee instance) {
      super(bindee);
      this.instance = instance;
    }

    @Override
    public final TBindee getReference() {
      return this.instance;
    }
  }

  private final Class<TBindee> bindee;
  private Binding<TBindee> binding;

  public ActivatorBindingImpl(final Class<TBindee> bindee) {
    this.bindee = bindee;
  }

  @Override
  public <TBound extends TBindee> ServerActivator.ActivatorBinding
  to(Class<TBound> type, ServerActivator.ActivationMode mode) {
    this.binding = new ClassBinding<>(this.bindee, type, mode);
    return this;
  }

  @Override
  public ServerActivator.ActivatorBinding toInstance(TBindee instance) {
    this.binding = new InstanceBinding<>(this.bindee, instance);
    return this;
  }
}
