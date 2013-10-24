package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public final class DiskPersistenceManager implements Persistence {

  private final HashFunction hasher = Hashing.md5();

  // The root path of the page store
  private final Path root;

  @Inject
  private DiskPersistenceManager(@Named("dfs.store.path") String rootPath) throws IOException {
    this.root = Paths.get(rootPath);
    if (!Files.exists(root)) {
      Files.createDirectory(root);
    } else {
      if (!Files.isDirectory(root)) {
        throw new NotDirectoryException(rootPath);
      }
    }
    if (!Files.isWritable(root)) {
      throw new AccessDeniedException(rootPath, null,
                                      "Root path for file persistence is not writable");
    }
  }

  /**
   * Gets an {@link OutputStream} for writing a single page associated with {@code id}.
   */
  @Override
  public OutputStream getOutputStream(PageIdentifier id) throws IOException {
    Path tableDirectory = root.resolve(makePathString(id.getTableName()));
    if (!Files.isDirectory(tableDirectory)) {
      FileUtils.deleteDirectory(tableDirectory.toFile());
    }

    Path fullPath = tableDirectory.resolve(makePathString(id.toString()));
    Files.deleteIfExists(fullPath);
    File file = fullPath.toFile();
    com.google.common.io.Files.createParentDirs(file);
    // Overwrite existing file
    return new BufferedOutputStream(new FileOutputStream(file));
  }

  /**
   * Gets an {@link InputStream} for reading a single page associated with {@code id}.
   */
  @Override
  public InputStream getInputStream(PageIdentifier id) throws IOException {
    Path fullPath = root.resolve(Paths.get(makePathString(id.getTableName()),
                                           makePathString(id.toString())));
    if (!Files.exists(fullPath)) {
      throw new NoSuchFileException(fullPath.toString());
    }
    if (!Files.isReadable(fullPath)) {
      throw new AccessDeniedException(fullPath.toString());
    }

    return new BufferedInputStream(new FileInputStream(fullPath.toFile()));
  }

  /**
   * Returns a normalized version of a string safe for filesystem paths.
   */
  private String makePathString(String s) {
    return hasher.hashString(s, Charsets.UTF_8).toString();
  }
}
