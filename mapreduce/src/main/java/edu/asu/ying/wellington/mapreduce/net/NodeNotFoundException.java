package edu.asu.ying.wellington.mapreduce.net;

import java.io.IOException;

/**
 *
 */
public class NodeNotFoundException extends IOException {

  public NodeNotFoundException(String identifier) {
    super(identifier);
  }

  public NodeNotFoundException(String identifier, Throwable cause) {
    super(identifier, cause);
  }
}
