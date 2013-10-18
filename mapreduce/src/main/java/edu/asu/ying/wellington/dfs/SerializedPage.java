package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public interface SerializedPage<K extends WritableComparable, V extends Writable>
    extends Page<K, V>, Iterable<SerializedElement<K, V>> {

}
