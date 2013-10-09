package edu.asu.ying.common.event;

/**
 *
 */
public interface EventProvider<E extends Event<?, ?>> {

  E getEvent();
}
