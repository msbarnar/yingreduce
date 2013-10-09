package edu.asu.ying.database.page;

import java.rmi.RemoteException;

import edu.asu.ying.common.sink.Sink;
import edu.asu.ying.p2p.rmi.AbstractExportable;

/**
 *
 */
public final class ServerPageSink extends AbstractExportable<RemotePageSink> implements Sink<Page> {

  public ServerPageSink() {
  }

  @Override
  public boolean offer(final Page page) throws RemoteException {
    System.out.println("Page received");
    return false;
  }

  @Override
  public int offer(final Iterable<Page> pages) throws RemoteException {
    System.out.println("Pages received");
    return 0;
  }
}
