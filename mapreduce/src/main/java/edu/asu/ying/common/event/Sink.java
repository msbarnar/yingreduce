package edu.asu.ying.common.event;

import java.io.IOException;

/**
 *
 */
public interface Sink<E> {

  boolean offer(E object) throws IOException;

  int offer(Iterable<E> objects) throws IOException;
}