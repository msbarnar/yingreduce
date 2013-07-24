package edu.asu.ying.mapreduce.net.kad;

import javax.annotation.Nonnull;

import edu.asu.ying.mapreduce.net.NodeURI;
import il.technion.ewolf.kbr.Key;

/**
 *
 */
public class KadNodeURI implements NodeURI {

  public static KadNodeURI fromKademliaKey(final Key key) {
    return new KadNodeURI(key.toBase64());
  }

  public static KadNodeURI fromStringKey(final String key) {
    return new KadNodeURI(key);
  }

  protected final Key key;

  protected KadNodeURI(final String key) {
    this.key = new Key(key);
  }

  public final Key toKademliaKey() {
    return this.key;
  }

  @Nonnull
  @Override
  public final String getKey() {
    return this.toString();
  }

  @Override
  public String toString() {
    return this.key.toBase64();
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null)
      return false;
    if (o == this)
      return true;

    if (!(o instanceof KadNodeURI))
      return false;

    final KadNodeURI uri = (KadNodeURI) o;
    return uri.toKademliaKey().equals(this.key);
  }

  @Override
  public int hashCode() {
    return this.key.hashCode();
  }
}
