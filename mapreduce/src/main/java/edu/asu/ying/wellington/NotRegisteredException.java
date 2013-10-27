package edu.asu.ying.wellington;

/**
 * Thrown when an application requests an instance by an unbound name.
 */
public class NotRegisteredException extends ServiceException {

  public NotRegisteredException(String name, Class<? extends Service> cls) {
    super(String.format("No instance of class %s named `%s` is registered.",
                        cls.getName(), name));
  }
}
