package edu.asu.ying.mapreduce.events;

public interface ValueChangedEventHandler<T>
	extends EventHandler<ValueChangedEventArgs<T>>
{
	public void onEvent(Object sender, ValueChangedEventArgs<T> args);
}
