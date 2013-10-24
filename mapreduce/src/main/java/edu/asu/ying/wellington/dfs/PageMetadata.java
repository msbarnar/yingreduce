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

  private static final long SerialVersionUID = 1L;

  private final PageIdentifier id;
  private int numElements;
  private final Class<K> keyClass;
  private final Class<V> valueClass;
  private final Collection<RemoteNode> containerNodes = new ArrayList<>();
  private int checksum;

  public PageMetadata(PageIdentifier id,
                      Class<K> keyClass,
                      Class<V> valueClass) {
    this.id = id;
    this.keyClass = keyClass;
    this.valueClass = valueClass;
  }

  public PageIdentifier getId() {
    return id;
  }

  public int size() {
    return numElements;
  }

  public void setNumElements(int numElements) {
    this.numElements = numElements;
  }

  public Class<K> getKeyClass() {
    return keyClass;
  }

  public Class<V> getValueClass() {
    return valueClass;
  }

  public void addContainerNodes(Collection<RemoteNode> containerNodes) {
    this.containerNodes.addAll(containerNodes);
  }

  public Collection<RemoteNode> getContainerNodes() {
    return containerNodes;
  }

  public void setChecksum(int checksum) {
    this.checksum = checksum;
  }

  public int getChecksum() {
    return checksum;
  }
}
