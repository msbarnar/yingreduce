package edu.asu.ying.mapreduce.common;

import java.io.IOException;

/**
 *
 */
public class Pipe {

  public static <E> SinkToRemoteSink<E> toRemoteSink(final RemoteSink<E> sink) {
    return new SinkToRemoteSink<>(sink);
  }

  /**
   * {@code SinkToRemoteSink} pipes a {@link Sink} to a {@link RemoteSink}.
   */
  public static class SinkToRemoteSink<E> implements Sink<E> {

    private final RemoteSink<E> remoteSink;

    private SinkToRemoteSink(final RemoteSink<E> remoteSink) {
      this.remoteSink = remoteSink;
    }

    @Override
    public void accept(final E e) throws IOException {
      this.remoteSink.accept(e);
    }
  }
}
