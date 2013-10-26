package edu.asu.ying.wellington.dfs;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.ybase.Element;

/**
 *
 */
public interface SerializedWritablePage<K extends WritableComparable, V extends Writable>
    extends HasPageMetadata<K, V>, Sink<Element<K, V>> {

}
