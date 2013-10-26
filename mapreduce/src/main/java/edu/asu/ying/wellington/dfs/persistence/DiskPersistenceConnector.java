package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public final class DiskPersistenceConnector implements PersistenceConnector {

  private static final Logger log = Logger.getLogger(DiskPersistenceConnector.class.getName());

  public static final String PROPERTY_STORE_PATH = "dfs.store.path";

  // Name of the file which stores the name of the table in the normalized table folder
  private static final String TABLE_NAME_FILENAME = ".table_name";

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
  public boolean exists(PageIdentifier id) {
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
      byte[] buffer = new byte[8092];
      int read = 0;
      while ((read = istream.read(buffer)) > 0) {
        checksummer.putBytes(buffer, 0, read);
      }
      return checksummer.hash().asInt() == checksum;
    }
  }

  /**
   * Finds all of the table directories and all of the page files in the store path.
   */
  @Override
  public Set<PageIdentifier> getAllStoredPages() throws IOException {
    Set<PageIdentifier> storedPages = new HashSet<>();

    File file = new File(root.toUri());
    // Get all directories
    String[] tableDirs = file.list(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return new File(dir, name).isDirectory();
      }
    });

    for (String tableDir : tableDirs) {
      Path tablePath = root.resolve(tableDir);
      String tableName = readTableName(tablePath);
      file = new File(tablePath.toUri());
      // Get all files that aren't the table name file
      String[] pageFiles = file.list(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return !name.equals(TABLE_NAME_FILENAME);
        }
      });
      // Add a page to the set for each file, where the page index is the filename
      for (String pageFile : pageFiles) {
        try {
          int pageIndex = Integer.valueOf(pageFile);
          storedPages.add(PageIdentifier.create(tableName, pageIndex));
        } catch (NumberFormatException e) {
          // Delete this errant file
          Path pagePath = tablePath.resolve(pageFile);
          Files.delete(pagePath);
          log.info("Pruned misnamed file from page store: ".concat(pagePath.toString()));
        }
      }
    }

    return storedPages;
  }

  /**
   * @throws FileAlreadyExistsException if the page is already stored.
   */
  @Override
  public OutputStream getOutputStream(PageIdentifier id) throws IOException {
    Path tableDirectory = createTableDirectory(id.getTableName());

    Path fullPath = tableDirectory.resolve(Integer.toString(id.getIndex()));
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

  private Path createTableDirectory(String tableName) throws IOException {
    Path path = root.resolve(makePathString(tableName));
    // If the table directory exists but is not a directory, delete it
    if (Files.exists(path)) {
      if (!Files.isDirectory(path)) {
        Files.delete(path);
        Files.createDirectory(path);
      }
    } else {
      Files.createDirectory(path);
    }

    // Write a file with the name of the table in the directory so we can recover the name later
    Path tableNameFile = path.resolve(TABLE_NAME_FILENAME);
    try (DataOutputStream ostream
             = new DataOutputStream(
        Files.newOutputStream(tableNameFile, StandardOpenOption.CREATE))) {
      ostream.writeUTF(tableName);
    }

    return path;
  }

  private String readTableName(Path tableDirectory) throws IOException {
    Path tableNameFile = tableDirectory.resolve(TABLE_NAME_FILENAME);
    try (DataInputStream istream
             = new DataInputStream(Files.newInputStream(tableNameFile, StandardOpenOption.READ))) {
      return istream.readUTF();
    }
  }
}
