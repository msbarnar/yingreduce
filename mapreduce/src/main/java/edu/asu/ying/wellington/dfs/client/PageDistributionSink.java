package edu.asu.ying.wellington.dfs.client;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.PageMetadata;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 * {@code PageDistributionSink} distributes accepted pages to appropriate peers on the network.
 */
public final class PageDistributionSink implements Sink<PageMetadata> {

  private static final int DEFAULT_DUPLICATION_FACTOR = 3;

  private final NodeLocator locator;

  private final int pageDuplicationFactor = DEFAULT_DUPLICATION_FACTOR;

  // Used to offer a page to several peers at once
  private final ExecutorService workersByNode = Executors.newCachedThreadPool();
  // Used to offer several pages at once
  private final ExecutorService workersByPage = Executors.newFixedThreadPool(3);


  @Inject
  private PageDistributionSink(NodeLocator locator) {
    this.locator = locator;
  }

  /**
   * Finds k peers near the pageMetadata's key and concurrently offers them the pageMetadata.
   */
  @Override
  public boolean offer(final PageMetadata pageMetadata) throws IOException {
    List<RemoteNode> nodes = findRecipientsFor(pageMetadata);
    List<Future<Boolean>> results = new ArrayList<>(nodes.size());

    // Concurrently offer the pageMetadata to all of the peers
    for (final RemoteNode node : nodes) {
      results.add(workersByNode.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          //return node.getDFSService().getPageDepository().offer(pageMetadata);
          return false;
        }
      }));
    }

    // Wait for each peer to return
    boolean success = true;
    for (Future<Boolean> result : results) {
      try {
        success &= result.get();
      } catch (InterruptedException | ExecutionException e) {
        // TODO: Logging
        e.printStackTrace();
        return false;
      }
    }
    return success;
  }

  /**
   * For each {@link edu.asu.ying.wellington.dfs.PageMetadata} in {@code pages}, finds k peers near
   * the page's key and concurrently
   * offers them the page.
   *
   * @return the number of pages successfully sent.
   */
  @Override
  public int offer(Iterable<PageMetadata> pages) throws IOException {
    List<Future<Boolean>> results = new ArrayList<>();
    for (final PageMetadata pageMetadata : pages) {
      results.add(workersByPage.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          return PageDistributionSink.this.offer(pageMetadata);
        }
      }));
    }

    int count = 0;
    for (Future<Boolean> result : results) {
      try {
        count += result.get() ? 1 : 0;
      } catch (InterruptedException | ExecutionException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }
    return count;
  }

  private List<RemoteNode> findRecipientsFor(PageMetadata pageMetadata) throws IOException {
    return locator.find(pageMetadata.getID().toString(), pageDuplicationFactor);
  }
}
