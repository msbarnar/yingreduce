package edu.asu.ying.common;

import java.io.IOException;

/**
 *
 */
public class VersionMismatchException extends IOException {

  public VersionMismatchException(int expected, int got) {
    super(String.format("Version mismatch: expected %d, got %d", expected, got));
  }
}
