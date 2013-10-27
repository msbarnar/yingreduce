package edu.asu.ying.wellington;

/**
 * The {@code ServiceLocator} is the core of the Wellington distributed service platform.
 * </p>
 * The service locator allows applications to find named instances of services regardless of their
 * location of medium.
 * </p>
 * When a service is registered with the service locator, that service instance is wrapped in a
 * proxy that abstracts the service implementation from its access medium. Wellington can then
 * use whatever means are appropriate to access the and provide transparent use of the instance to
 * other applications using the locator.
 */
public interface ServiceLocator {

  /**
   * Binds the class-specific {@code name} to {@code instance}.
   */
  <T extends Service> void register(String name, T instance) throws AlreadyRegisteredException;

  /**
   * Gets the instance of {@code cls} bound to {@code name}.
   *
   * @throws NotRegisteredException if no such binding exists.
   * @throws ServiceImportException if the instance is unreachable.
   */
  <T extends Service> T get(String name, Class<T> cls) throws ServiceException;
}
