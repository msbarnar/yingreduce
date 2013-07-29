package edu.asu.ying.mapreduce.common.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * The base event type. Calls {@link EventHandler#onEvent} with arguments when fired.
 */
public abstract class Event<TEventHandler extends EventHandler<TEventArgs>, TEventArgs> {

  protected final List<TEventHandler> handlers = new ArrayList<>();

  protected Event() {
  }

  public void attach(final TEventHandler handler) {
    synchronized (this.handlers) {
      this.handlers.add(handler);
    }
  }

  public boolean detach(final TEventHandler handler) {
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
  public void fire(final Object sender, final @Nullable TEventArgs args) {
    synchronized (this.handlers) {
      final Iterator<TEventHandler> iter = this.handlers.iterator();
      while (iter.hasNext()) {
        if (!iter.next().onEvent(sender, args)) {
          iter.remove();
        }
      }
    }
  }

  public Object getResponse(final Object sender, final @Nullable TEventArgs args) {
    synchronized (this.handlers) {
      if (this.handlers.size() > 0) {
        return this.handlers.get(0).onRequest(sender, args);
      } else {
        return null;
      }
    }
  }
}