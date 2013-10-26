package edu.asu.ying.wellington.dfs.client;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;

import edu.asu.ying.wellington.dfs.File;
import edu.asu.ying.wellington.dfs.File.OutputMode;

/**
 *
 */
public final class DFSClient {

  private final Provider<OutputStream> pageOutputStreamProvider;

  @Inject
  private DFSClient(Provider<OutputStream> pageOutputStreamProvider) {

    this.pageOutputStreamProvider = pageOutputStreamProvider;
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
  }

  /**
   * Gets an input stream reading from {@code file}.
   *
   * @throws java.io.FileNotFoundException if the file doesn't exist.
   * @throws SecurityException             if the caller's privileges don't satisfy the file's
   *                                       {@link edu.asu.ying.wellington.dfs.SecurityAttributes}.
   */
  public InputStream getInputStream(File file) throws IOException, SecurityException {
    // TODO: Bind a BufferedPageInputStream to file
  }
}
