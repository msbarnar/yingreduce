package edu.asu.ying.p2p.node.kad;

import java.net.URI;

import edu.asu.ying.p2p.node.NodeURL;
import il.technion.ewolf.kbr.Key;

/**
 *
 */
public final class KadNodeURL extends KadNodeIdentifier implements NodeURL {

  private final URI uri;

  public KadNodeURL(final String key, final String uri) {
    super(key);
    this.uri = URI.create(uri);
  }

  public KadNodeURL(final Key key, final URI uri) {
    super(key);
    this.uri = uri;
  }

  public KadNodeURL(final String uri) {
    this(null, uri);
  }

  @Override
  public URI toURI() {
    // TODO: Ports from configuration
    return URI.create(String.format("openkad.udp://%s:%d/",
                                    this.uri.getHost(), this.uri.getPort()));
  }

  @Override
  public final String toString() {
    return super.toString().concat(": ").concat(this.uri.toString());
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
    return url.toURI().equals(this.uri);
  }

  @Override
  public final int hashCode() {
    return super.hashCode() ^ this.uri.hashCode();
  }
}
