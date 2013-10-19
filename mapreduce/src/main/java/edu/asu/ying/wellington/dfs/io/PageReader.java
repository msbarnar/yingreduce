package edu.asu.ying.wellington.dfs.io;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public interface PageReader<K extends WritableComparable, V extends Writable>
    extends Iterable<Element<K, V>> {

  Page<K, V> getMetadata();
}
