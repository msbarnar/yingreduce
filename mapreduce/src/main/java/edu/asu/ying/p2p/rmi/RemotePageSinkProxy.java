package edu.asu.ying.p2p.rmi;

import java.io.IOException;
import java.rmi.RemoteException;

import edu.asu.ying.common.sink.Sink;
import edu.asu.ying.database.page.Page;
import edu.asu.ying.database.page.RemotePageSink;

/**
 *
 */
public final class RemotePageSinkProxy implements RemotePageSink {

  private final Sink<Page> localSink;

  public RemotePageSinkProxy(final Sink<Page> sink) {
    this.localSink = sink;
  }

  @Override
  public boolean offer(final Page page) throws RemoteException {
    try {
      return this.localSink.offer(page);
    } catch (final IOException e) {
      throw new RemoteException("Remote page sink threw an exception", e);
    }
  }

  @Override
  public int offer(final Iterable<Page> pages) throws RemoteException {
    try {
      return this.localSink.offer(pages);
    } catch (final IOException e) {
      throw new RemoteException("Remote page sink threw an exception", e);
    }
  }
}
