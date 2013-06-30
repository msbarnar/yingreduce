package edu.asu.ying.mapreduce.rmi;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Marks a field or parameters as receiving an injected server-side {@link Activator}
 * implementation. <p> Apply to a method with {@link com.google.inject.Provides} to provide an
 * implementation of that source.
 */
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface ServerActivator {

}
