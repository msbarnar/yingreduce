package edu.asu.ying.wellington.dfs;

import java.io.IOException;

/**
 *
 */
public class ChecksumMismatchException extends IOException {

  public ChecksumMismatchException(int expected, int actual) {
    super(String.format("Checksum mismatch: expected `%d`, got `%d`", expected, actual));
  }
}
