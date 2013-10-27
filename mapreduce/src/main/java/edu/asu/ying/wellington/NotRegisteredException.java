package edu.asu.ying.wellington;

import edu.asu.ying.wellington.service.Service;
import edu.asu.ying.wellington.service.ServiceException;

/**
 * Thrown when an application requests an instance by an unbound name.
 */
public class NotRegisteredException extends ServiceException {

  public NotRegisteredException(String name, Class<? extends Service> cls) {
    super(String.format("No instance of class %s named `%s` is registered.",
                        cls.getName(), name));
  }
}
