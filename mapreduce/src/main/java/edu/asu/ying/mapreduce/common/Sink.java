package edu.asu.ying.mapreduce.common;

import java.io.IOException;

/**
 *
 */
public interface Sink<TObject> {

  void accept(final TObject object) throws IOException;
}
