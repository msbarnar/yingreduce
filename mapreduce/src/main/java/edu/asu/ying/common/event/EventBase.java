package edu.asu.ying.common.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * The base event type. Calls {@link EventHandler#onEvent} with arguments when fired.
 */
public class EventBase<T extends EventHandler<A>, A> implements Event<T, A> {

  protected final List<T> handlers = new ArrayList<>();

  public EventBase() {
  }

  @Override
  public void attach(final T handler) {
    synchronized (this.handlers) {
      this.handlers.add(handler);
    }
  }

  @Override
  public boolean detach(final T handler) {
    synchronized (this.handlers) {
      return this.handlers.remove(handler);
    }
  }

  /**
   * Notifies all event handlers, passing {@code args}. </p> If a handler returns {@code false} from
   * its {@link EventHandler#onEvent} method, it is detached from this event.
   *
   * @param sender the object firing the event.
   * @param args   optional arguments passed to event handlers.
   */
  @Override
  public void fire(final Object sender, final @Nullable A args) {
    synchronized (this.handlers) {
      final Iterator<T> iter = this.handlers.iterator();
      while (iter.hasNext()) {
        if (!iter.next().onEvent(sender, args)) {
          iter.remove();
        }
      }
    }
  }
}