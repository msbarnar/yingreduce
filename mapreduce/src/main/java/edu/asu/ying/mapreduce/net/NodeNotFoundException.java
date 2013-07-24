package edu.asu.ying.mapreduce.net;

import java.io.IOException;

/**
 *
 */
public class NodeNotFoundException extends IOException {

  public NodeNotFoundException(final NodeURL address) {
    super(address.toString());
  }
}
