package edu.asu.ying.mapreduce.database.page;

import java.rmi.RemoteException;

/**
 * {@code RemotePageSinkImpl} accepts pages from the network and stores them locally.
 */
public final class RemotePageSinkImpl implements RemotePageSink {

  @Override
  public final void offer(final Page page) throws RemoteException {
    System.out.println("Got remote page!");
  }
}
