package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * A page whose elements can be iterated. The source of the elements is undefined; they may be
 * read from a stream or from a collection.
 */
public interface ReadablePage<K extends WritableComparable, V extends Writable>
    extends HasPageMetadata<K, V>, Iterable<Element<K, V>> {

}
