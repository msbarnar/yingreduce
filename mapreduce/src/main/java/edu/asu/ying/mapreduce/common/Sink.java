package edu.asu.ying.mapreduce.common;

import java.io.IOException;

/**
 *
 */
public interface Sink<TObject> {

  boolean offer(final TObject object) throws IOException;

  int offer(final Iterable<TObject> objects) throws IOException;
}
