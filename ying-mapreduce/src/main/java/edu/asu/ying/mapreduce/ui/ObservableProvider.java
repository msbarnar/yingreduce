package edu.asu.ying.mapreduce.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a registry for {@link Observable} types that can be referenced
 * by class or by arbitrary name.
 */
public enum ObservableProvider {
	INSTANCE;
	
	private final Map<Object, Observable> registry = new HashMap<Object, Observable>();
	
	private ObservableProvider() {
	}
	
	public final void register(final Observable observable) {
		this.registry.put(observable.getClass(), observable);
	}
	public final void register(final String name, final Observable observable) {
		this.registry.put(name, observable);
	}
		
	public final Observable getObservable(final Class<?> clazz) {
		return this.registry.get(clazz);
	}
	public final Observable getObservable(final String name) {
		return this.registry.get(name);
	}
}
