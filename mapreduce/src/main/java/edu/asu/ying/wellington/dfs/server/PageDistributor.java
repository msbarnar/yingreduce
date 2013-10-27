package edu.asu.ying.wellington.dfs.server;

import javax.annotation.Nullable;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.PageData;

/**
 * A {@code PageDistributor} is a sink for {@link PageData} that distributes the pages received to
 * the network by way of the local {@link edu.asu.ying.wellington.dfs.DFSService}.
 */
public interface PageDistributor extends Sink<PageData> {

  void start();

  void notifyResult(String transferId, @Nullable Throwable exception);
}
