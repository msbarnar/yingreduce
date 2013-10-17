package edu.asu.ying.wellington.dfs.io;

import java.io.Closeable;
import java.io.IOException;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.Page;

/**
 *
 */
public interface ElementReader extends Page, Closeable {

  /**
   * Returns the next element from the page, or {@code null} if no elements are remaining.
   */
  Element nextElement() throws IOException;
}
