package edu.asu.ying.common.event;

import java.io.IOException;

/**
 *
 */
public interface Sink<E> {

  void accept(E object) throws IOException;
}
