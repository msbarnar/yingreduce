package edu.asu.ying.mapreduce.messaging;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;


/**
 * {@link SendMessageSink} annotates a field that is intended to be injected with a {@link MessageSink} chain has a
 * network transmitter as its final sink.
 * <p>
 * Annotating a {@link MessageSink} parameter with this ensures that messages sent to that sink will be sent to a
 * remote host.
 */
@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface SendMessageSink {}
