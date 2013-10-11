package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.server.ExportException;

/**
 *
 */
public interface Activator {

  /**
   * The {@code ActivationMode} specifies the manner in which objects are instantiated to fulfill
   * bindings.
   */
  enum ActivationMode {
    Singleton,
    SingleCall
  }

  /**
   * A {@code Binding} fulfills a request for an object of type {@code TBound} by providing an
   * instance of {@code TBound} or one of its subclasses.
   */
  interface Binding<TBound extends Activatable> {

    /**
     * Provides a fully exported {@link Remote} proxy of type {@code TBound}.
     */
    TBound getReference();
  }

  /**
   * {@code Binder} provides helper functions for binding a class of type {@code TBound} to a
   * subclass or instance of {@code TBound}.
   */
  interface Binder<TBound extends Activatable> {

    /**
     * Binds {@code TBound} to {@code type}, instantiating {@code type} as requested according to
     * the given {@link ActivationMode}.
     *
     * @param type      the type which will be instantiated to fulfill {@code TBound}.
     * @param mode      the manner of instantiation (e.g. singleton).
     * @param <TBindee> the type of the class bound.
     */
    <TBindee extends TBound> TBound to(Class<TBindee> type,
                                       Activator.ActivationMode mode);

    /**
     * Binds {@code TBound} to a specific instance of {@code TBound}. </p> This binding will only
     * ever provide {@code instance} to fulfill {@code TBound}.
     */
    TBound toInstance(TBound instance);

    <TBindee extends TBound> ViaBinder<TBound> via(Class<TBindee> proxyClass);

    /**
     * Returns the binding created by one of {@link Binder#to} or {@link Binder#toInstance}.
     */
    Binding getBinding();
  }

  interface ViaBinder<TBound extends Activatable> {

    /**
     * Binds {@code TBound} to a specific instance of {@code TBound}. </p> This binding will only
     * ever provide {@code instance} to fulfill {@code TBound}.
     */
    TBound toInstance(Object instance) throws ExportException;
  }

  /**
   * Creates a binding of the class {@code type} to an instance or subclass of {@code type}. If a
   * subclass is bound, it will be instantiated according to the {@link ActivationMode} given. </p>
   * When a class is activated, the instance provided will be determined by bindings made with
   * {@link Activator#bind}.
   *
   * @param type     the class to bind.
   * @param <TBound> the type of the bound class.
   * @return a {@link Binder} which is used to further specify the type of the binding.
   */
  <TBound extends Activatable> Binder<TBound> bind(Class<TBound> type);

  <T extends Activatable> T getReference(Class<? extends Activatable> cls);

  int getPort();
}
