package edu.asu.ying.mapreduce.mapreduce.net;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;

/**
 *
 */
public final class NodeIdentifier implements Serializable, Comparable<NodeIdentifier> {

  private static final long SerialVersionUID = 1L;

  private final String id;

  public NodeIdentifier(String id) {
    this.id = Preconditions.checkNotNull(Strings.emptyToNull((id)));
  }

  @Override
  public int compareTo(NodeIdentifier o) {
    return this.id.compareTo(o.id);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && this.id
        .equals(((NodeIdentifier) o).id);
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return this.id;
  }
}
