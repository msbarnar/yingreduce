package edu.asu.ying.p2p.rmi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.server.ExportException;

/**
 *
 */
public abstract class Exportable<T extends Remote> {

  // Keep the proxy alive by keeping the glue class in scope
  private T proxyTarget;
  // Provide access to the RMI exported object
  private T proxy;
  private Class<T> clazz;

  public final <V extends T> void export(final Class<V> proxyClass, final RMIActivator activator)
      throws
      ExportException {
    try {
      final Constructor<V> ctor = proxyClass.getConstructor(this.getClass());
      this.proxyTarget = ctor.newInstance(this);
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

    this.proxy = activator.bind(clazz).toInstance(this.proxyTarget);
  }

  public final T getProxy() {
    return this.proxy;
  }
}
