package edu.asu.ying.wellington.dfs;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public interface SerializingPage<K extends WritableComparable, V extends Writable>
    extends PageMetadata<K, V>, Iterable<SerializedElement<K, V>>, Sink<Element<K, V>> {

}