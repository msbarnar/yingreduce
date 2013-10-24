package edu.asu.ying.wellington.dfs.io;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.ReadablePage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * Serializes an entire page to an output stream provided by a {@link PageOutputStreamProvider}.
 * <p/>
 * The filesystem does not currently support multiple pages per file (or per memory cache record),
 * so the stream provider should provide a unique stream for each unique page identifier.
 */
public interface PageWriter {

  /**
   * Serializes an entire page to the underlying stream in the following sequence:
   * <ol>
   * <li>The header (see: {@link PageHeader})</li>
   * <li>Serialized key->value pairs</li>
   * </ol>
   */
  <K extends WritableComparable, V extends Writable>
  void write(ReadablePage<K, V> p) throws IOException;
}
