package edu.asu.ying.database.page;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.asu.ying.common.sink.Sink;

/**
 * {@code PageSinkQueue} manages the flow of pages into a {@link Sink}. </p> This is used for page
 * distribution to throttle concurrent {@code pageout}s to the network.
 */
public final class PageSinkQueue extends AbstractSinkFIFO<Page> {

  public PageSinkQueue(final Sink<Page> pageSink) {
    super(pageSink);
  }

  @Override
  protected ExecutorService createThreadPool() {
    return Executors.newSingleThreadExecutor();
  }
}
