package edu.asu.ying.mapreduce.messaging;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;


/**
 * Marks a field or parameters as receiving an injected {@link edu.asu.ying.mapreduce.messaging.io.MessageOutputStream} that will send messages to
 * remote hosts.
 * <p>
 * Apply to a method with {@link com.google.inject.Provides} to provide an implementation of that stream.
 */
@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface SendMessageStream {}
