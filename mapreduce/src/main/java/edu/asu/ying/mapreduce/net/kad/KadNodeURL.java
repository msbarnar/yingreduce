package edu.asu.ying.mapreduce.net.kad;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 */
public class KadNodeURL extends KadNodeURI {

  protected KadNodeURL(final InetAddress address) {
    super(null);
    this.address = address;
  }

  @Override
  public URI toURI() throws URISyntaxException {
    // TODO: Ports from configuration
    return new URI(String.format("openkad.udp://%s:5888/", this.address.getHostAddress()));
  }
}
