package edu.asu.ying.mapreduce.database.element;

import com.google.common.primitives.Longs;

import java.util.Comparator;

/**
 * Compares elements by the size of their {@link Element.Value}.
 */
public class ElementSizeComparator implements Comparator<Element> {

  @Override
  public int compare(final Element a, final Element b) {
    return Longs.compare(a.getValue().getSize(), b.getValue().getSize());
  }
}
