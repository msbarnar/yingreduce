package edu.asu.ying.common.event;

import javax.annotation.Nullable;

/**
 *
 */
public interface Event<T extends EventHandler<A>, A> {

  void attach(T handler);

  boolean detach(T handler);

  void fire(Object sender, @Nullable A args);
}
