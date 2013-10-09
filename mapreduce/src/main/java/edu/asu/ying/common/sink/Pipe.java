package edu.asu.ying.common.sink;

import java.io.IOException;

/**
 *
 */
public final class Pipe {

  public static <E> SinkToRemoteSink<E> toRemoteSink(final RemoteSink<E> sink) {
    return new SinkToRemoteSink<>(sink);
  }

  /**
   * {@code SinkToRemoteSink} pipes a {@link Sink} to a {@link RemoteSink}.
   */
  private static class SinkToRemoteSink<E> implements Sink<E> {

    private final RemoteSink<E> remoteSink;

    private SinkToRemoteSink(final RemoteSink<E> remoteSink) {
      this.remoteSink = remoteSink;
    }

    @Override
    public boolean offer(final E object) throws IOException {
      return this.remoteSink.offer(object);
    }

    @Override
    public int offer(final Iterable<E> objects) throws IOException {
      return this.remoteSink.offer(objects);
    }
  }
}
