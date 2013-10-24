package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public interface ReadablePage<K extends WritableComparable, V extends Writable>
    extends HasPageMetadata<K, V>, Iterable<Element<K, V>> {

}
