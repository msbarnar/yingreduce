package edu.asu.ying.mapreduce.net.kad;

import java.net.InetAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.net.NodeURI;
import il.technion.ewolf.kbr.Key;

/**
 *
 */
public final class KadNodeURI implements NodeURI {

  public static KadNodeURI fromKademliaKey(final Key key) {
    return new KadNodeURI(key.toBase64());
  }

  public static KadNodeURI fromStringKey(final String key) {
    return new KadNodeURI(key);
  }

  private final Key key;
  private final InetAddress address;

  protected KadNodeURI(final String key) {
    this.key = new Key(key);
    this.address = null;
  }

  public Key toKademliaKey() {
    return this.key;
  }

  @Override
  public final String toString() {
    return this.key.toBase64();
  }

  @Override
  public final boolean equals(final Object o) {
    if (o == null)
      return false;
    if (o == this)
      return true;

    if (!(o instanceof KadNodeURI))
      return false;

    final KadNodeURI uri = (KadNodeURI) o;
    return uri.toKademliaKey().equals(this.key) && uri.getAddress().equals(this.address);
  }

  @Override
  public final int hashCode() {
    return this.key.hashCode() ^ this.address.hashCode();
  }

  @Nonnull
  @Override
  public String getKey() {
    return this.toString();
  }

  @Nullable
  @Override
  public InetAddress getAddress() {
    return this.address;
  }
}
