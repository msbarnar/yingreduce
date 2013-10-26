package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.dfs.PageName;

/**
 * A {@code Persistence} provides access to pages on the local machine, controlling caching and
 * storage.
 */
public interface Persistence {

  /**
   * Reads a page from {@code stream} and stores it locally as page {@code id}.
   *
   * @throws InvalidPageException      if the data in the stream are not a valid page.
   * @throws ChecksumMismatchException if the checksum in the stream header does not match the
   *                                   data.
   */
  void storePage(PageName id, InputStream stream) throws IOException;

  /**
   * Gets an input stream from page {@code id} which reads the page's raw data, including the
   * header. A sensible page can be produced by reading this with a {@link PageDeserializer}.
   */
  InputStream readPage(PageName id) throws IOException;

  /**
   * Returns {@code true} if page {@code id} is available locally.
   */
  boolean hasPage(PageName id);
}
