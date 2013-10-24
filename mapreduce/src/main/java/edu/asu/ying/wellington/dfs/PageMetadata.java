package edu.asu.ying.wellington.dfs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 *
 */
public final class PageMetadata<K extends WritableComparable, V extends Writable>
    implements Serializable {

  private final PageIdentifier id;
  private final int numElements;
  private final Class<K> keyClass;
  private final Class<V> valueClass;
  private final Collection<RemoteNode> containerNodes = new ArrayList<>();
  private final int checksum;

  public PageMetadata(PageIdentifier id,
                      int numElements,
                      Class<K> keyClass,
                      Class<V> valueClass,
                      Collection<RemoteNode> containerNodes,
                      int checksum) {
    this.id = id;
    this.numElements = numElements;
    this.keyClass = keyClass;
    this.valueClass = valueClass;
    this.containerNodes.addAll(containerNodes);
    this.checksum = checksum;
  }

  public PageIdentifier getId() {
    return id;
  }

  public int size() {
    return numElements;
  }

  public Class<K> getKeyClass() {
    return keyClass;
  }

  public Class<V> getValueClass() {
    return valueClass;
  }

  public Collection<RemoteNode> getContainerNodes() {
    return containerNodes;
  }

  public int getChecksum() {
    return checksum;
  }
}
