package edu.asu.ying.common.event;

import javax.annotation.Nullable;


/**
 * The interface defining a callback method to which event can pass arguments when they are fired.
 *
 * @param <A> the type which contains contextual information about the {@link EventBase}.
 */
public interface EventHandler<A> {

  boolean onEvent(final Object sender, final @Nullable A args);
}
