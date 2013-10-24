package edu.asu.ying.wellington.dfs;

import java.io.IOException;

import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class ElementTooLargeException extends IOException {

  public ElementTooLargeException() {
  }

  public ElementTooLargeException(final WritableComparable key) {
    super("The element exceeds the maximum capacity of the page: ".concat(key.toString()));
  }
}
