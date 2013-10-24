package edu.asu.ying.wellington.dfs.io;

import java.util.Iterator;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.ReadablePage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@code PageReader} provides iterators
 */
public final class PageReader<K extends WritableComparable, V extends Writable>
    implements ReadablePage<K, V> {

  public PageReader() {

  }

  @Override
  public Iterator<Element<K, V>> iterator() {
    return null;
  }

  @Override
  public PageIdentifier getId() {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Class<K> getKeyClass() {
    return null;
  }

  @Override
  public Class<V> getValueClass() {
    return null;
  }
}
