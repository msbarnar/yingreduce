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
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import edu.asu.ying.wellington.dfs.PageName;

/**
 *
 */
public final class DiskPersistenceConnector implements PersistenceConnector {

  private static final Logger log = Logger.getLogger(DiskPersistenceConnector.class.getName());

  public static final String PROPERTY_STORE_PATH = "dfs.store.path";

  // Name of the file which stores the name of the table in the normalized table folder
  private static final String FILE_PATH_FILENAME = ".filepath";

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
  public boolean exists(PageName page) {
    return Files.exists(getPagePath(page));
  }

  @Override
  public boolean deleteIfExists(PageName page) throws IOException {
    return Files.deleteIfExists(getPagePath(page));
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
    // Get the path to the page file
    Path path = createFileDirectory(name.path()).resolve(Integer.toString(name.index()));
    // FIXME: Allow overwriting
    Files.deleteIfExists(path);
    return new BufferedOutputStream(new FileOutputStream(path.toFile()));
  }

  /**
   * @throws NoSuchFileException   if there's no file for the indicated page.
   * @throws AccessDeniedException if the file is not readable.
   */
  @Override
  public InputStream getInputStream(PageName page) throws IOException {
    Path pagePath = getPagePath(page);
    if (!Files.exists(pagePath)) {
      throw new NoSuchFileException(pagePath.toString());
    }
    if (!Files.isReadable(pagePath)) {
      throw new AccessDeniedException(pagePath.toString());
    }

    return new BufferedInputStream(new FileInputStream(pagePath.toFile()));
  }

  /**
   * Serializes the page index to a file.
   */
  @Override
  public void savePageIndex(Set<PageName> pageIndex) throws IOException {
  }

  /**
   * Deserializes the page index from a file.
   */
  @Override
  public Set<PageName> loadPageIndex() throws IOException {
    Set<PageName> loadedIndex = new HashSet<>();
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
    return storedPages;
  }

  /**
   * Returns a normalized version of a string safe for filesystem paths.
   */
  private String getPathString(String s) {
    return pathNormalizer.hashString(s, Charsets.UTF_8).toString();
  }

  /**
   * Returns a normalized path, prefixed with the root store path, for the given file.
   */
  private Path getFilePath(edu.asu.ying.wellington.dfs.Path filePath) {
    return root.resolve(getPathString(filePath.toString()));
  }

  private Path getPagePath(PageName page) {
    return getFilePath(page.path()).resolve(Integer.toString(page.index()));
  }

  /**
   * Creates the directory hierarchy for the file and writes a special file to the leaf directory
   * with the plaintext file path and name.
   */
  private Path createFileDirectory(edu.asu.ying.wellington.dfs.Path filePath) throws IOException {
    Path path = getFilePath(filePath);
    // If the file directory exists but is not a directory, delete it
    if (Files.exists(path)) {
      if (!Files.isDirectory(path)) {
        Files.delete(path);
        Files.createDirectory(path);
      }
    } else {
      // Create the directory tree
      Files.createDirectories(path);
    }

    // Write a file with the plaintext path of the file in the directory so we can recover the name
    // later
    Path pathFileName = path.resolve(FILE_PATH_FILENAME);
    if (!Files.exists(pathFileName)) {
      Files.write(pathFileName, filePath.toString().getBytes(Charsets.UTF_8),
                  StandardOpenOption.CREATE);
    }

    return path;
  }

  /**
   * Reads the plaintext file path and name from the special file left in the file directory.
   */
  private String readPathFile(Path path) throws IOException {
    Path tableNameFile = path.resolve(FILE_PATH_FILENAME);
    return Files.readAllLines(tableNameFile, Charsets.UTF_8).get(0);
  }
}
