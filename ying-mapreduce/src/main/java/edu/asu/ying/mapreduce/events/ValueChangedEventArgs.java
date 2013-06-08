package edu.asu.ying.mapreduce.events;

public final class ValueChangedEventArgs<T>
	extends EventArgs
{
	private final T value;
	
	public ValueChangedEventArgs(final T newValue) {
		this.value = newValue;
	}
	
	public final T getValue() {
		return this.value;
	}
}
