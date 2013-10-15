package edu.asu.ying.common.event;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 *
 */
public final class Pipe {

  public static <E> Sink<E> toRemoteSink(RemoteSink<E> sink) {
    return new SinkToRemoteSink<>(sink);
  }

  public static <E> RemoteSink<E> toSink(Sink<E> sink) {
    return new RemoteSinkToSink<>(sink);
  }

  /**
   * {@code SinkToRemoteSink} pipes a {@link Sink} to a {@link RemoteSink}.
   */
  private static class SinkToRemoteSink<E> implements Sink<E> {

    private final RemoteSink<E> remoteSink;

    private SinkToRemoteSink(RemoteSink<E> remoteSink) {
      this.remoteSink = remoteSink;
    }

    @Override
    public boolean offer(E object) throws IOException {
      return remoteSink.offer(object);
    }

    @Override
    public int offer(Iterable<E> objects) throws IOException {
      return remoteSink.offer(objects);
    }
  }

  private static class RemoteSinkToSink<E> implements RemoteSink<E> {

    private final Sink<E> sink;

    private RemoteSinkToSink(Sink<E> sink) {
      this.sink = sink;
    }

    @Override
    public boolean offer(E object) throws RemoteException {
      try {
        return sink.offer(object);
      } catch (IOException e) {
        throw new RemoteException("Remote sink threw an exception", e);
      }
    }

    @Override
    public int offer(Iterable<E> objects) throws RemoteException {
      try {
        return sink.offer(objects);
      } catch (IOException e) {
        throw new RemoteException("Remote sink threw an exception", e);
      }
    }
  }
}
