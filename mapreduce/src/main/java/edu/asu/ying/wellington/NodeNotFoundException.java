package edu.asu.ying.wellington;

import java.io.IOException;

/**
 *
 */
public class NodeNotFoundException extends IOException {

  public NodeNotFoundException(String name) {
    super(name);
  }

  public NodeNotFoundException(String name, Throwable cause) {
    super(name, cause);
  }
}
