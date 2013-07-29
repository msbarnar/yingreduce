package edu.asu.ying.mapreduce.common.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * The interface defining a callback method to which event can pass arguments when they are fired.
 *
 * @param <TEventArgs> the type which contains contextual information about the {@link Event}.
 */
public interface EventHandler<TEventArgs> {

  boolean onEvent(final @Nonnull Object sender, final @Nullable TEventArgs args);
  Object onRequest(final @Nonnull Object sender, final @Nullable TEventArgs args);
}
