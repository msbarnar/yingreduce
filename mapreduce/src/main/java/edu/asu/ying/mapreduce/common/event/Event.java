package edu.asu.ying.mapreduce.common.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

public abstract class Event<TEventHandler, TEventArgs> {

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

  public void fire(final Object sender, final @Nullable TEventArgs args) {
    synchronized (this.handlers) {
      final Iterator<TEventHandler> iter = this.handlers.iterator();
      while (iter.hasNext()) {
        if (!this.fireHandler(iter.next(), sender, args)) {
          iter.remove();
        }
      }
    }
  }

  public Object getResponse(final Object sender, final @Nullable TEventArgs args) {
    synchronized (this.handlers) {
      if (this.handlers.size() > 0) {
        return this.requestHandler(this.handlers.get(0), sender, args);
      } else {
        return null;
      }
    }
  }

  protected abstract boolean fireHandler(final TEventHandler handler, final Object sender,
                                         final TEventArgs args);
  protected abstract Object requestHandler(final TEventHandler handler, final Object sender,
                                         final TEventArgs args);
}