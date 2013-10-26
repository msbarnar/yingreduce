package edu.asu.ying.wellington.dfs;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.wellington.dfs.server.PageResponsibilityRecord;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 *
 */
public final class PageMetadata<K extends WritableComparable, V extends Writable>
    implements Serializable {

  private static final Logger log = Logger.getLogger(PageMetadata.class.getName());

  private static final long SerialVersionUID = 1L;

  private final PageIdentifier id;
  private int numElements;
  private final Class<K> keyClass;
  private final Class<V> valueClass;
  private final Collection<PageResponsibilityRecord> responsibilityTable = new ArrayList<>();
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

  public void addResponsibleNode(RemoteNode node) {
    try {
      responsibilityTable.add(new PageResponsibilityRecord(id, node.getName(), node));
    } catch (RemoteException e) {
      log.log(Level.WARNING, "Exception getting name from remote node", e);
    }
  }

  public void addResponsibleNode(String name) {
    responsibilityTable.add(new PageResponsibilityRecord(id, name));
  }

  public void addResponsibleNode(PageResponsibilityRecord record) {
    if (!record.getPageId().equals(id)) {
      throw new IllegalArgumentException("Page responsibility record is not for this page");
    }
    responsibilityTable.add(record);
  }

  /**
   * Returns the records of which nodes are responsible for (have copies of) this page.
   */
  public Collection<PageResponsibilityRecord> getResponsibilityTable() {
    return responsibilityTable;
  }

  public void removeResponsibleNode(String name) {
    for (Iterator<PageResponsibilityRecord> iter = responsibilityTable.iterator();
         iter.hasNext(); ) {
      if (iter.next().getNodeName().equals(name)) {
        iter.remove();
      }
    }
  }

  public void removeResponsibleNode(RemoteNode node) {
    for (Iterator<PageResponsibilityRecord> iter = responsibilityTable.iterator();
         iter.hasNext(); ) {
      if (node.equals(iter.next().getNode())) {
        iter.remove();
      }
    }
  }

  public void setChecksum(int checksum) {
    this.checksum = checksum;
  }

  public int getChecksum() {
    return checksum;
  }
}
