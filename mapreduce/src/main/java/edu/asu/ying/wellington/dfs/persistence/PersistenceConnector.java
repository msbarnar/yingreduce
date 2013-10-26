package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import edu.asu.ying.wellington.dfs.PageName;

/**
 * A {@code PersistenceConnector} connects the {@link PersistenceEngine} to some concrete local
 * storage model, e.g. a memory cache or a SQL database.
 */
public interface PersistenceConnector {

  void savePageIndex(Set<PageName> index) throws IOException;

  Set<PageName> loadPageIndex() throws IOException;

  Set<PageName> rebuildPageIndex() throws IOException;

  /**
   * Returns {@code true} if a resource exists for page {@code id}.
   */
  boolean exists(PageName id);

  /**
   * Deletes the resource for page {@code id}, returning {@code true} if the resource was deleted
   * or {@code false} if the resource does not exist.
   */
  boolean deleteIfExists(PageName id) throws IOException;

  /**
   * Returns {@code true} if a 32-bit checksum of the file matches {@code checksum}.
   */
  boolean validate(PageName id, int checksum) throws IOException;

  /**
   * Gets an output stream to a resource for page {@code id}, creating it if necessary.
   *
   * @throws PageExistsException if the resource already exists.
   */
  OutputStream getOutputStream(PageName id) throws IOException;

  /**
   * Gets an input stream from a resource.
   *
   * @throws PageNotFoundException if the page is not available from this connector.
   */
  InputStream getInputStream(PageName id) throws IOException;
}
