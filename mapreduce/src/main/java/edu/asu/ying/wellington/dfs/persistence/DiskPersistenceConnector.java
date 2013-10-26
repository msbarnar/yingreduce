package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public final class DiskPersistenceConnector implements PersistenceConnector {

  public static final String PROPERTY_STORE_PATH = "dfs.store.path";

  private final HashFunction pathNormalizer = Hashing.md5();
  private final HashFunction checksumFunc = Hashing.adler32();

  // The root path of the page store
  private final Path root;

  /**
   * Creates the connector with a root path for all pages.
   *
   * @param rootPath the path in which all pages and metadata will be stored.
   */
  @Inject
  private DiskPersistenceConnector(@Named(PROPERTY_STORE_PATH) String rootPath) throws IOException {
    this.root = Paths.get(rootPath);
    if (!Files.exists(root)) {
      Files.createDirectory(root);
    } else {
      if (!Files.isDirectory(root)) {
        throw new NotDirectoryException(rootPath);
      }
    }
    if (!Files.isWritable(root)) {
      throw new AccessDeniedException(rootPath, null, "Root path for persistence is not writable");
    }
  }

  @Override
  public boolean doesResourceExist(PageIdentifier id) {
    return Files.exists(makePath(id));
  }

  @Override
  public boolean deleteIfExists(PageIdentifier id) throws IOException {
    return Files.deleteIfExists(makePath(id));
  }

  @Override
  public boolean validate(PageIdentifier id, int checksum) throws IOException {
    // We shouldn't cache these because we might not be the only ones touching the filesystem,
    // though we should be.
    try (InputStream istream = getInputStream(id)) {
      Hasher checksummer = checksumFunc.newHasher();
      byte[] buffer = new byte[1024];
      int read = 0;
      while ((read = istream.read(buffer)) > 0) {
        checksummer.putBytes(buffer, 0, read);
      }
      return checksummer.hash().asInt() == checksum;
    }
  }

  /**
   * @throws FileAlreadyExistsException if the page is already stored.
   */
  @Override
  public OutputStream getOutputStream(PageIdentifier id) throws IOException {
    Path tableDirectory = root.resolve(makePathString(id.getTableName()));
    // If the table directory exists but is not a directory, delete it
    if (!Files.isDirectory(tableDirectory)) {
      Files.delete(tableDirectory);
    }

    Path fullPath = tableDirectory.resolve(makePathString(id.toString()));
    // Don't automatically overwrite files
    if (Files.exists(fullPath)) {
      throw new FileAlreadyExistsException(fullPath.toString());
    }
    File file = fullPath.toFile();
    // Creates the necessary directory hierarchy if it doesn't exist
    com.google.common.io.Files.createParentDirs(file);
    return new BufferedOutputStream(new FileOutputStream(file));
  }

  /**
   * @throws NoSuchFileException   if there's no file for the indicated page.
   * @throws AccessDeniedException if the file is not readable.
   */
  @Override
  public InputStream getInputStream(PageIdentifier id) throws IOException {
    Path fullPath = makePath(id);
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
    return pathNormalizer.hashString(s, Charsets.UTF_8).toString();
  }

  /**
   * Returns a normalized path, prefixed with the root store path, for the given page.
   */
  private Path makePath(PageIdentifier id) {
    return root.resolve(
        Paths.get(makePathString(id.getTableName()), makePathString(id.toString())));
  }
}
