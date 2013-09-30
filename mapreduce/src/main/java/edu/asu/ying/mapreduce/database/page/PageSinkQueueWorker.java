package edu.asu.ying.mapreduce.database.page;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.asu.ying.mapreduce.common.Sink;
import edu.asu.ying.mapreduce.common.concurrency.sink.AbstractSinkFIFO;

/**
 * {@code PageSinkQueueWorker} manages the flow of pages into a {@link Sink}.
 * </p>
 * This is used for page distribution to throttle concurrent {@code pageout}s to the network.
 */
public final class PageSinkQueueWorker extends AbstractSinkFIFO<Page> {

  public PageSinkQueueWorker(final Sink<Page> pageSink) {
    super(pageSink);
  }

  @Override
  protected ExecutorService createThreadPool() {
    return Executors.newSingleThreadExecutor();
  }
}
