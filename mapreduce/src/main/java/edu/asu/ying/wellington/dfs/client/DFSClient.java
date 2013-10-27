package edu.asu.ying.wellington.dfs.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.File;
import edu.asu.ying.wellington.dfs.File.OutputMode;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.PageData;
import edu.asu.ying.wellington.dfs.io.PageDistributionStream;
import edu.asu.ying.wellington.dfs.server.PageDistributor;
import edu.asu.ying.wellington.io.WritableInt;

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

    // TODO: Bind a PageDistributionStream to file
    switch (mode) {
      case CreateNew:
        // TODO: Check security
        // Set the page capacity
        file.properties().put(File.Properties.PageCapacity.toString(),
                              new WritableInt(pageCapacity));
        // Create an output stream with a buffer of `capacity` starting from page 0
        return new PageDistributionStream(Page.firstPageOf(file), pageDistributor);

      case Overwrite:
        // TODO: Check security
        // TODO: Delete existing pages
        break;

      case Append:
        // TODO: Check security
        // TODO: Start at end of pages
        break;
    }

    throw new IllegalArgumentException("Unknown output mode: ".concat(mode.toString()));
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
