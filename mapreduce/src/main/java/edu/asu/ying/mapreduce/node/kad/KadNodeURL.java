package edu.asu.ying.mapreduce.node.kad;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.node.NodeURL;

/**
 *
 */
public final class KadNodeURL extends KadNodeURI implements NodeURL {

  private final InetAddress address;

  protected KadNodeURL(final InetAddress address) {
    super(null);
    this.address = address;
  }

  @Override
  public URI toURI() throws URISyntaxException {
    // TODO: Ports from configuration
    return new URI(String.format("openkad.udp://%s:5888/", this.address.getHostAddress()));
  }

  @Nullable
  @Override
  public InetAddress getAddress() {
    return this.address;
  }

  @Override
  public final String toString() {
    return super.toString().concat(": ").concat(this.address.toString());
  }

  @Override
  public final boolean equals(final Object o) {
    // Compare the URI components as well as the URL
    if (!super.equals(o))
      return false;

    if (o == this)
      return true;

    if (!(o instanceof KadNodeURL))
      return false;

    final KadNodeURL url = (KadNodeURL) o;
    return url.getAddress().equals(this.address);
  }

  @Override
  public final int hashCode() {
    return super.hashCode() ^ this.address.hashCode();
  }
}
