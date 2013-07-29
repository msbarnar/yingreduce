package edu.asu.ying.mapreduce.node.kad;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;

import edu.asu.ying.mapreduce.node.NodeURI;
import il.technion.ewolf.kbr.Key;

/**
 *
 */
public class KadNodeURI implements NodeURI {

  protected final Key key;

  public KadNodeURI(final String key) {
    this.key = new Key(key);
  }

  public KadNodeURI(final Key key) {
    this.key = key;
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
    if (this.key == null) {
      return "";
    } else {
      return Strings.nullToEmpty(this.key.toBase64());
    }
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
