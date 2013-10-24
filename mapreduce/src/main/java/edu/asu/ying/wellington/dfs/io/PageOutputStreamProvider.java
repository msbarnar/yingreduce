package edu.asu.ying.wellington.dfs.io;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 * Provides output streams for writing pages.
 * <p/>
 * Because the filesystem does not currently support multiple pages per file (or per memory cache
 * record), {@link PageOutputStreamProvider#getStream(PageIdentifier)} should return a unique
 * stream for each unique page identifier.
 */
public interface PageOutputStreamProvider {

  /**
   * Gets an output stream for a particular page identifier. This stream should be unique to each
   * that particular identifier.
   */
  OutputStream getStream(PageIdentifier id) throws IOException;
}
