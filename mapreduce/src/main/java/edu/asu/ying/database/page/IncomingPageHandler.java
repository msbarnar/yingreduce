package edu.asu.ying.database.page;

import java.io.IOException;

import edu.asu.ying.common.event.EventBase;
import edu.asu.ying.common.event.EventHandler;
import edu.asu.ying.common.event.Sink;

/**
 *
 */
public final class IncomingPageHandler implements Sink<Page> {

  public final EventBase<EventHandler<Page>, Page> onIncomingPage = new EventBase<>();

  public IncomingPageHandler() {
  }

  @Override
  public boolean offer(final Page page) throws IOException {
    this.onIncomingPage.fire(this, page);
    return true;
  }

  @Override
  public int offer(final Iterable<Page> pages) throws IOException {
    int count = 0;
    for (final Page page : pages) {
      this.onIncomingPage.fire(this, page);
      count++;
    }
    return count;
  }
}
