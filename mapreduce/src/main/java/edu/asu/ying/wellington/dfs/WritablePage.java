package edu.asu.ying.wellington.dfs;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.ybase.Element;

/**
 * A page that is writable. The underlying sink is undefined; elements may be written to a stream
 * or to a collection.
 */
public interface WritablePage<K extends WritableComparable, V extends Writable>
    extends HasPageMetadata<K, V>, Sink<Element<K, V>> {

}
