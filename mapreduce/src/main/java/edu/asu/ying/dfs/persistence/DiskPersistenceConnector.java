package edu.asu.ying.dfs.persistence;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.dfs.PageName;

/**
 *
 */
public final class DiskPersistenceConnector implements PersistenceConnector {

  private static final Logger log = Logger.getLogger(DiskPersistenceConnector.class.getName());

  public static final String PROPERTY_STORE_PATH = "dfs.store.path";

  // Name of the file which stores the name of the table in the normalized table folder
  private static final String TABLE_NAME_FILENAME = ".table_name";

  // For normalizing table names to make them filesystem friendly
  private final HashFunction pathNormalizer = Hashing.md5();
  // For validating page contents
  private final HashFunction checksumFunc = Hashing.adler32();

  // The root path of the page store
  private final Path root;

  // For loading and saving the page index
  private static final String PAGE_INDEX_NAME = "pages.idx";
  private final File pageIndexFile;
  private final File pageIndexFileBak;

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

    this.pageIndexFile = new File(Paths.get(rootPath, PAGE_INDEX_NAME).toUri());
    this.pageIndexFileBak = new File(Paths.get(rootPath, "~" + PAGE_INDEX_NAME).toUri());
  }

  @Override
  public boolean exists(PageName id) {
    return Files.exists(makePath(id));
  }

  @Override
  public boolean deleteIfExists(PageName id) throws IOException {
    return Files.deleteIfExists(makePath(id));
  }

  @Override
  public boolean validate(PageName id, int checksum) throws IOException {
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
   * @throws FileAlreadyExistsException if the page is already stored.
   */
  @Override
  public OutputStream getOutputStream(PageName name) throws IOException {
   /* Path tableDirectory = createTableDirectory(id.getTableName());

    Path fullPath = tableDirectory.resolve(Integer.toString(id.index()));
    // Don't automatically overwrite files
    if (Files.exists(fullPath)) {
      throw new FileAlreadyExistsException(fullPath.toString());
    }
    File file = fullPath.toFile();
    // Creates the necessary directory hierarchy if it doesn't exist
    com.google.common.io.Files.createParentDirs(file);
    return new BufferedOutputStream(new FileOutputStream(file));*/
    // FIXME: get output stream
    return null;
  }

  /**
   * @throws NoSuchFileException   if there's no file for the indicated page.
   * @throws AccessDeniedException if the file is not readable.
   */
  @Override
  public InputStream getInputStream(PageName name) throws IOException {
    Path fullPath = makePath(name);
    if (!Files.exists(fullPath)) {
      throw new NoSuchFileException(fullPath.toString());
    }
    if (!Files.isReadable(fullPath)) {
      throw new AccessDeniedException(fullPath.toString());
    }

    return new BufferedInputStream(new FileInputStream(fullPath.toFile()));
  }

  /**
   * Serializes the page index to a file.
   */
  @Override
  public void savePageIndex(Set<PageName> pageIndex) throws IOException {
    // Don't allow saving and loading to interleave
    synchronized (pageIndexFile) {
      // Back up the index
      Files.copy(pageIndexFile.toPath(), pageIndexFileBak.toPath(),
                 StandardCopyOption.REPLACE_EXISTING);

      // Delete existing
      Files.deleteIfExists(pageIndexFile.toPath());
      // Write the index
      try (DataOutputStream ostream
               = new DataOutputStream(
          new BufferedOutputStream(new FileOutputStream(pageIndexFile)))) {
        // Start with the number of entries in the index
        ostream.writeInt(pageIndex.size());
        for (PageName id : pageIndex) {
          id.write(ostream);
        }
      } catch (Exception e) {
        // Copy the backup back to the regular file
        Files.copy(pageIndexFileBak.toPath(), pageIndexFile.toPath(),
                   StandardCopyOption.REPLACE_EXISTING);
        log.log(Level.WARNING, "Exception saving page index", e);
      }
    }
  }

  /**
   * Deserializes the page index from a file.
   */
  @Override
  public Set<PageName> loadPageIndex() throws IOException {
    Set<PageName> loadedIndex = new HashSet<>();
    // Don't allow saving and loading to interleave
    synchronized (pageIndexFile) {
      try (DataInputStream istream
               = new DataInputStream(new BufferedInputStream(new FileInputStream(pageIndexFile)))) {
        // Read the number of entries from the file
        for (int i = 0; i < istream.readInt(); i++) {
          loadedIndex.add(PageName.readFrom(istream));
        }
      }
    }

    return loadedIndex;
  }

  /**
   * Finds all of the table directories and all of the page files in the store path.
   * </p>
   * Does not expect that every directory in the root is a table directory, but does expect
   * that every file in a table directory is a page. Deletes any files that are not named
   * appropriately.
   */
  @Override
  public Set<PageName> rebuildPageIndex() throws IOException {
    Set<PageName> storedPages = new HashSet<>();

    File file = new File(root.toUri());
    // Get all directories
    try (DirectoryStream<Path> paths = Files.newDirectoryStream(root)) {
      for (Path tablePath : paths) {
        // Only look at directories
        if (!Files.isDirectory(tablePath)) {
          continue;
        }
        // Get the name of the table from the directory
        String tableName;
        try {
          tableName = readTableName(tablePath);
        } catch (FileNotFoundException e) {
          // Not a table directory
          continue;
        }
        // Each file should be a page
        try (DirectoryStream<Path> files = Files.newDirectoryStream(tablePath)) {
          for (Path pageFile : files) {
            String fileName = pageFile.getFileName().toString();
            // Skip the table name file
            if (fileName.equals(TABLE_NAME_FILENAME)) {
              continue;
            }
            // Use the filename as the page index; delete any files that don't have integral names
            try {
              int pageIndex = Integer.valueOf(fileName);
              // FIXME: PageName is different
              //storedPages.add(PageName.create(tableName, pageIndex));
            } catch (NumberFormatException e) {
              // Delete this errant file
              Files.delete(pageFile);
              log.info("Pruned misnamed file from page store: ".concat(pageFile.toString()));
            }
          }
        }
      }
    }

    return storedPages;
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
  private Path makePath(PageName id) {
    // FIXME: make path from pagename
    return null;
  }

  /**
   * Normalizes the table name to a filesystem-friendly name and ensures that a directory exists
   * by that name.
   * </p>
   * If one does not exist, also creates a file in that directory containing the name of the table
   * for later retrieval.
   */
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
    if (!Files.exists(tableNameFile)) {
      Files.write(tableNameFile, tableName.getBytes(Charsets.UTF_8), StandardOpenOption.CREATE);
    }

    return path;
  }

  /**
   * Reads the name of the table from a special file in the table directory, whose name is
   * normalized.
   */
  private String readTableName(Path tableDirectory) throws IOException {
    Path tableNameFile = tableDirectory.resolve(TABLE_NAME_FILENAME);
    return Files.readAllLines(tableDirectory, Charsets.UTF_8).get(0);
  }
}
