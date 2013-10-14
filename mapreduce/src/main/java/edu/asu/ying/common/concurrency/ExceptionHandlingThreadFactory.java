package edu.asu.ying.common.concurrency;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

/**
 *
 */
public final class ExceptionHandlingThreadFactory implements ThreadFactory {

  private final Thread.UncaughtExceptionHandler handler;

  public ExceptionHandlingThreadFactory(Thread.UncaughtExceptionHandler handler) {
    this.handler = handler;
  }

  @Override
  @NotNull
  public Thread newThread(@NotNull Runnable target) {
    final Thread thread = new Thread(target);
    thread.setUncaughtExceptionHandler(this.handler);
    return thread;
  }
}
