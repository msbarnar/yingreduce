package edu.asu.ying.mapreduce.events;

/**
 * The interface defining a callback method to which events can pass {@link EventArgs} when they are fired.
 * @param <TEventArgs> the type extending {@link EventArgs} which contains contextual information about the {@link Event}.
 */
public interface EventHandler<TEventArgs extends EventArgs>
{
	public void onEvent(final Object sender, final TEventArgs args);
}
