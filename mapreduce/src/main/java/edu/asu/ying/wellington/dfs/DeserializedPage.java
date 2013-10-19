package edu.asu.ying.wellington.dfs;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public interface DeserializedPage<K extends WritableComparable, V extends Writable>
    extends Page<K, V>, Iterable<Element<K, V>>, Sink<Element<K, V>> {

}
