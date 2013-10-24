package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * A page that is both readable and writable, e.g. a collection of elements.
 */
public interface Page<K extends WritableComparable, V extends Writable>
    extends ReadablePage<K, V>, WritablePage<K, V> {

}
