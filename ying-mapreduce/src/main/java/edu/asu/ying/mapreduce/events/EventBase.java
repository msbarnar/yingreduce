/*
 * Event.java
 * Base class for callback events. 
 * Represents something happening with no state passed.
 */
package edu.asu.ying.mapreduce.events;

import java.util.Vector;

/**
 * The base event type. Calls {@link EventHandler#onEvent} passing {@link EventArgs} when fired.
 * @param TEventHandler the type implementing {@link EventHandler#onEvent} that handles this event. The {@link EventHandler#onEvent} method
 * must accept a parameter of type <code>TEventArgs</code>.
 * @param TEventArgs the type of argument extending {@link EventArgs} passed to the event handler on firing.
 */
public class EventBase<TEventHandler extends EventHandler<TEventArgs>, TEventArgs extends EventArgs>
{
	protected Vector<TEventHandler> handlers = new Vector<TEventHandler>();
	
	public EventBase() {
	}
	
	public void attach(final TEventHandler handler) {
		this.handlers.add(handler);
	}
	public boolean detach(final TEventHandler handler) {
		return this.handlers.remove(handler);
	}
	
	public void fire(final Object sender, final TEventArgs args) {
		for (final TEventHandler handler : this.handlers) {
			handler.onEvent(sender, args);
		}
	}
}