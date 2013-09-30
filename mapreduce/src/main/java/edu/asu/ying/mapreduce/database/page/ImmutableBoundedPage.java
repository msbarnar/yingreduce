package edu.asu.ying.mapreduce.database.page;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.database.element.Element;
import edu.asu.ying.mapreduce.database.table.TableID;

/**
 *
 */
public final class ImmutableBoundedPage implements Page {

  private static final long SerialVersionUID = 1L;

  private final TableID tableId;
  // The index of this page on the table
  private final int index;

  private final Map<Element.Key, Element.Value> elements = new HashMap<Element.Key, Element.Value>();
  // Don't accept any elements that would cause the page to exceed this size
  private final int capacity;
  // Keep track of the total size of the contents of all elements
  private int curSize = 0;

  // True if a change has been made since committing the page
  private boolean isDirty = false;

  public ImmutableBoundedPage(final TableID parentTableId,
                              final int index,
                              final int capacity) {

    this.tableId = parentTableId;

    this.index = index;
    this.capacity = capacity;
  }


  public final void addElement(final Element element) throws PageCapacityExceededException {
    synchronized (this.elements) {
      if ((this.curSize + element.getValue().getSize()) > this.capacity) {
        throw new PageCapacityExceededException(element);
      }

      this.elements.put(element.getKey(), element.getValue());
      this.curSize += element.getValue().getSize();

      this.isDirty = true;
    }
  }

  
  public final TableID getTableId() {
    return this.tableId;
  }

  
  public final int getIndex() {
    return this.index;
  }

  
  public final int getSize() {
    synchronized (this.elements) {
      return this.curSize;
    }
  }

  
  public final int getCapacity() {
    return this.capacity;
  }

  
  public final boolean isDirty() {
    synchronized (this.elements) {
      return this.isDirty;
    }
  }

  
  public final void clean() {
    synchronized (this.elements) {
      this.isDirty = false;
    }
  }

  
  public final ImmutableMap<Element.Key, Element.Value> getElements() {
    return ImmutableMap.copyOf(this.elements);
  }
}
