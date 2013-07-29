package edu.asu.ying.mapreduce.node;

import java.io.IOException;

/**
 *
 */
public class NodeNotFoundException extends IOException {

  public NodeNotFoundException(final NodeURL address) {
    super(address.toString());
  }
}
