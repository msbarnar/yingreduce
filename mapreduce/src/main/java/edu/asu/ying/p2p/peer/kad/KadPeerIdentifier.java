package edu.asu.ying.p2p.peer.kad;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;

import edu.asu.ying.p2p.PeerIdentifier;
import il.technion.ewolf.kbr.Key;

/**
 *
 */
public class KadPeerIdentifier implements PeerIdentifier {

  protected final Key key;

  public KadPeerIdentifier(final String key) {
    this.key = new Key(key);
  }

  public KadPeerIdentifier(final Key key) {
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
      return Strings.nullToEmpty(this.key.toString());
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }

    if (!(o instanceof KadPeerIdentifier)) {
      return false;
    }

    final KadPeerIdentifier uri = (KadPeerIdentifier) o;
    return uri.toKademliaKey().equals(this.key);
  }

  @Override
  public int hashCode() {
    return this.key.hashCode();
  }
}
