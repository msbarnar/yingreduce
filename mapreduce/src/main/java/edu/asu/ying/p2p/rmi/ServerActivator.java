package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import javax.annotation.Nullable;

import edu.asu.ying.p2p.RemoteNode;

/**
 *
 */
public interface ServerActivator {

  /**
   * The {@code ActivationMode} specifies the manner in which objects are instantiated to fulfill
   * bindings.
   */
  public enum ActivationMode {
    Singleton,
    SingleCall
  }

  /**
   * A {@code Binding} fulfills a request for an object of type {@code TBound} by providing an
   * instance of {@code TBound} or one of its subclasses.
   */
  public interface Binding<TBound extends Remote> {

    /**
     * Provides a fully exported {@link Remote} proxy of type {@code TBound}.
     */
    TBound getReference();
  }

  /**
   * {@code Binder} provides helper functions for binding a class of type {@code TBound} to a
   * subclass or instance of {@code TBound}.
   */
  public interface Binder<TBound extends Remote> {

    /**
     * Binds {@code TBound} to {@code type}, instantiating {@code type} as requested according to
     * the given {@link ActivationMode}.
     * @param type the type which will be instantiated to fulfill {@code TBound}.
     * @param mode the manner of instantiation (e.g. singleton).
     * @param <TBindee> the type of the class bound.
     */
    <TBindee extends TBound> void to(Class<TBindee> type,
                                     ServerActivator.ActivationMode mode);

    /**
     * Binds {@code TBound} to a specific instance of {@code TBound}.
     * </p>
     * This binding will only ever provide {@code instance} to fulfill {@code TBound}.
     */
    void toInstance(TBound instance);

    /**
     * Returns the binding created by one of {@link Binder#to} or {@link Binder#toInstance}.
     */
    Binding getBinding();
  }

  /**
   * Creates a binding of the class {@code type} to an instance or subclass of {@code type}.
   * If a subclass is bound, it will be instantiated according to the {@link ActivationMode} given.
   * </p>
   * When a class is activated, the instance provided will be determined by bindings made with
   * {@link ServerActivator#bind}.
   * @param type the class to bind.
   * @param <TBound> the type of the bound class.
   * @return a {@link Binder} which is used to further specify the type of the binding.
   */
  <TBound extends Remote> Binder<TBound> bind(Class<TBound> type);

  <TBound extends Remote> TBound getReference(final Class<TBound> type,
                                              final @Nullable Map<String, String> properties)
      throws RemoteException;

  int getPort();
}
