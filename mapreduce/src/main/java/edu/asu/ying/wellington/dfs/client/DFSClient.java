package edu.asu.ying.wellington.dfs.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.File;
import edu.asu.ying.wellington.dfs.File.OutputMode;
import edu.asu.ying.wellington.dfs.PageData;
import edu.asu.ying.wellington.dfs.io.BufferedPageOutputStream;
import edu.asu.ying.wellington.dfs.server.PageDistributor;

/**
 *
 */
public final class DFSClient {

  private static final String PROPERTY_PAGE_CAPACITY = "dfs.page.capacity";

  private final Sink<PageData> pageDistributor;
  private int pageCapacity;

  @Inject
  private DFSClient(@PageDistributor Sink<PageData> pageDistributor,
                    @Named(PROPERTY_PAGE_CAPACITY) int pageCapacity) {

    if (pageCapacity <= 0) {
      throw new IllegalArgumentException(PROPERTY_PAGE_CAPACITY.concat(" must be >0"));
    }

    this.pageDistributor = pageDistributor;
    this.pageCapacity = pageCapacity;
  }

  /**
   * Gets an output stream writing to {@code file}.
   *
   * @throws FileAlreadyExistsException if {@code mode} is {@link OutputMode.CreateNew} and the
   *                                    file
   *                                    exists.
   * @throws SecurityException          if the caller's privileges don't satisfy the file's
   *                                    {@link edu.asu.ying.wellington.dfs.SecurityAttributes}.
   */
  public OutputStream getOutputStream(File file, OutputMode mode)
      throws IOException, SecurityException {

    // TODO: Check security

    // TODO: Bind a BufferedPageOutputStream to file
    switch (mode) {
      case CreateNew:
        // TODO: Fail if file exists
        break;

      case Overwrite:
        // TODO: Delete existing pages
        break;

      case Append:
        // TODO: Start at end of pages
        break;
    }

    return new BufferedPageOutputStream(pageDistributor, pageCapacity);
  }

  /**
   * Gets an input stream reading from {@code file}.
   *
   * @throws java.io.FileNotFoundException if the file doesn't exist.
   * @throws SecurityException             if the caller's privileges don't satisfy the file's
   *                                       {@link edu.asu.ying.wellington.dfs.SecurityAttributes}.
   */
  /*public InputStream getInputStream(File file) throws IOException, SecurityException {
    // TODO: Bind a BufferedPageInputStream to file
  }*/
}
