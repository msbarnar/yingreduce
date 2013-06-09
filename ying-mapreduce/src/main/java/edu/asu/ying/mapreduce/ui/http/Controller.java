package edu.asu.ying.mapreduce.ui.http;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

/**
 * Whereas the {@link Presenter} receives changes from the daemon and updates the interface,
 * the {@link Controller} 
 */
public abstract class Controller 
	extends HttpServlet {
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface WebMethod {
		String name();
	}
	
	private static final long serialVersionUID = -7262662107449993079L;
	
	protected Controller() {
		this.registerActions();
	}
	
	// Map of action strings to methods
	protected Map<String, Method> actions = new HashMap<String, Method>();
	
	protected void registerActions() {
		final Method[] methods = this.getClass().getMethods();
		for (final Method method : methods) {
			final WebMethod actionAnnot = method.getAnnotation(WebMethod.class);
			if (actionAnnot == null) {
				continue;
			}
			this.actions.put(actionAnnot.name(), method);
		}
	}
}
