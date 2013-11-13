package edu.asu.ying.common.event;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import edu.asu.ying.common.filter.Filter;


/**
 * When fired, {@code FilteredValueEvent} only notifies event handlers whose {@link Filter} matches
 * the value fired.
 */
public class FilteredValueEventBase<TValue>
    implements FilteredValueEvent<TValue> {

  private final
  List<Map.Entry<Filter, EventHandler<TValue>>>
      handlers =
      new ArrayList<>();

  /**
   * Attaches an event handler that will be notified when a value matching {@code filter} arrives.
   *
   * @param filter  the filter which selects values of which to notify {@code handler}.
   * @param handler the handler to be notified.
   */

  public final void attach(final Filter filter, final EventHandler<TValue> handler) {
    synchronized (this.handlers) {
      this.handlers.add(new AbstractMap.SimpleEntry<>(filter, handler));
    }
  }

  /**
   * Detaches a {@link Filter}, {@link EventHandler} pair.
   *
   * @param filter  the filter with which the handler was attached.
   * @param handler the handler previously attached handler.
   * @return true if the handler was detached.
   */

  public final boolean detach(final Filter filter, final EventHandler<TValue> handler) {
    synchronized (this.handlers) {
      return this.handlers
          .remove(new AbstractMap.SimpleEntry<>(filter, handler));
    }
  }

  /**
   * Notifies event handlers whose filter matches {@code value} that value has arrived. </p> If a
   * handler returns {@code false} from its {@link EventHandler#onEvent} method, the handler and
   * its
   * filter are detached from this event.
   *
   * @param sender the object firing the event, from whom the {@code value} came.
   * @param value  the value of which to notify event handlers.
   */

  public final void fire(final Object sender, final @Nullable TValue value) {
    synchronized (this.handlers) {
      final Iterator<Map.Entry<Filter, EventHandler<TValue>>> iter = this.handlers.iterator();
      while (iter.hasNext()) {
        final Map.Entry<Filter, EventHandler<TValue>> handler = iter.next();
        if (handler.getKey().match(value)) {
          // A return value of false means the handler is refusing further event
          if (!handler.getValue().onEvent(sender, value)) {
            iter.remove();
          }
        }
      }
    }
  }
}
