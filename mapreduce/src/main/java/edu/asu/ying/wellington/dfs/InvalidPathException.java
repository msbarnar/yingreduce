package edu.asu.ying.wellington.dfs;

import java.io.IOException;

/**
 *
 */
public class InvalidPathException extends IOException {

  public InvalidPathException(String path) {
    super("Invalid path: ".concat(path));
  }
}
