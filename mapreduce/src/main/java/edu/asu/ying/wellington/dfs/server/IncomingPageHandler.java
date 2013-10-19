package edu.asu.ying.wellington.dfs.server;

import java.io.IOException;

import edu.asu.ying.common.event.EventBase;
import edu.asu.ying.common.event.EventHandler;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.PageMetadata;

/**
 *
 */
public final class IncomingPageHandler implements Sink<PageMetadata> {

  public final
  EventBase<EventHandler<PageMetadata>, PageMetadata>
      onIncomingPage =
      new EventBase<>();

  public IncomingPageHandler() {
  }

  @Override
  public boolean offer(final PageMetadata pageMetadata) throws IOException {
    this.onIncomingPage.fire(this, pageMetadata);
    return true;
  }

  @Override
  public int offer(final Iterable<PageMetadata> pages) throws IOException {
    int count = 0;
    for (final PageMetadata pageMetadata : pages) {
      this.onIncomingPage.fire(this, pageMetadata);
      count++;
    }
    return count;
  }
}
