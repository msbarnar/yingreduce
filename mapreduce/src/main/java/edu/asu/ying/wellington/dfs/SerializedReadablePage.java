package edu.asu.ying.wellington.dfs;

import java.io.InputStream;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public interface SerializedReadablePage<K extends WritableComparable, V extends Writable>
    extends HasPageMetadata<K, V>, Iterable<SerializedElement<K, V>> {

  /**
   * Returns the entire content of the serialized page, including the header.
   */
  byte[] toByteArray();

  InputStream getInputStream();
}
