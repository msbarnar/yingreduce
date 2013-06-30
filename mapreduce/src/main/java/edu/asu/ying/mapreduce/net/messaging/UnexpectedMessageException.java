package edu.asu.ying.mapreduce.net.messaging;

import java.io.IOException;

/**
 * Signals that an object received a message of the wrong type and cannot handle the message
 * received.
 */
public final class UnexpectedMessageException
    extends IOException {

  private final Class<? extends Message> expected;
  private final Message received;

  public UnexpectedMessageException(final Message received,
                                    final Class<? extends Message> expected) {
    this.received = received;
    this.expected = expected;
  }

  public final Message getReceived() {
    return this.received;
  }

  public final Class<? extends Message> getExpected() {
    return this.expected;
  }
}
