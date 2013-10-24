package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public interface Page<K extends WritableComparable, V extends Writable>
    extends ReadablePage<K, V>, WritablePage<K, V> {

}
