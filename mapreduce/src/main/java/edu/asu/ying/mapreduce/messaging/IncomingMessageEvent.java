package edu.asu.ying.mapreduce.messaging;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;


/**
 * Marks a field or parameters as receiving an injected message source that provides incoming messages from the network.
 * <p>
 * Apply to a method with {@link com.google.inject.Provides} to provide an implementation of that source.
 */
@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface IncomingMessageEvent
{}
