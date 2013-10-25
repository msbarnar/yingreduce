package edu.asu.ying.common.concurrency;

/**
 *
 */
public final class DelegateQueueExecutor<T> extends QueueExecutor<T> {

  private final QueueProcessor<T> processor;

  public DelegateQueueExecutor(QueueProcessor<T> processor) {
    this.processor = processor;
  }

  @Override
  protected void process(T task) {
    try {
      processor.process(task);
    } catch (Exception e) {
      // FIXME: Handle
      throw new RuntimeException(e);
    }
  }
}
