package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 * A {@code PersistenceConnector} connects the {@link PersistenceEngine} to some concrete local
 * storage model, e.g. a memory cache or a SQL database.
 */
public interface PersistenceConnector {

  /**
   * Returns {@code true} if a resource exists for page {@code id}.
   */
  boolean doesResourceExist(PageIdentifier id);

  /**
   * Deletes the resource for page {@code id}, returning {@code true} if the resource was deleted
   * or {@code false} if the resource does not exist.
   */
  boolean deleteIfExists(PageIdentifier id) throws IOException;

  /**
   * Returns {@code true} if a 32-bit checksum of the file matches {@code checksum}.
   */
  boolean validate(PageIdentifier id, int checksum) throws IOException;

  /**
   * Gets an output stream to a resource for page {@code id}, creating it if necessary.
   *
   * @throws PageExistsException if the resource already exists.
   */
  OutputStream getOutputStream(PageIdentifier id) throws IOException;

  /**
   * Gets an input stream from a resource.
   *
   * @throws PageNotFoundException if the page is not available from this connector.
   */
  InputStream getInputStream(PageIdentifier id) throws IOException;
}
