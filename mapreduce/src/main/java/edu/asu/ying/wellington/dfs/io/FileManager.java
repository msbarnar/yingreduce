package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

import edu.asu.ying.wellington.AbstractIdentifier;
import edu.asu.ying.wellington.dfs.NameProvider;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public final class FileManager {

  private final HashFunction hasher = Hashing.md5();

  private final Path root;

  @Inject
  private FileManager(NameProvider names) {
    this.root = names.getRootPath();
  }

  public PageOutputStream createNew(Page page) throws IOException {
    PageIdentifier pageID = page.getPageID();
    String tableFolder = makePathString(pageID.getTableID());
    Path path = root.resolve(tableFolder);
    checkMkdir(path);

    String pageFile = makePathString(pageID);
    path = path.resolve(pageFile);

    Files.deleteIfExists(path);
    // Overwrite existing file
    OutputStream stream = new BufferedOutputStream(new FileOutputStream(path.toFile(), false));
    return new PageOutputStream(stream);
  }

  /*public IndexSeekingInputStream openExisting(PageIdentifier identifier) throws IOException {
    String tableFolder = makePathString(identifier.getTableID());

    Path path = root.resolve(tableFolder);
    checkDirectory(path);

    String pageFile = makePathString(identifier);
    path = path.resolve(pageFile);
    checkReadable(path);

    InputStream stream = new BufferedInputStream(new FileInputStream(path.toFile()));
    return new IndexSeekingInputStream(stream);
  }*/

  private String makePathString(AbstractIdentifier identifier) {
    HashCode hash = hasher.hashString(identifier.toString(), Charsets.UTF_8);
    return hash.toString();
  }

  private void checkMkdir(Path path) throws IOException {
    if (!Files.exists(path)) {
      Files.createDirectory(path);
    } else {
      if (!Files.isDirectory(path)) {
        throw new NotDirectoryException(path.toString());
      }
    }
  }

  private void checkDirectory(Path path) throws IOException {
    if (!Files.exists(path) || !Files.isDirectory(path)) {
      throw new NotDirectoryException(path.toString());
    }
    if (!Files.isReadable(path)) {
      throw new AccessDeniedException(path.toString());
    }
  }

  private void checkReadable(Path path) throws IOException {
    if (!Files.exists(path)) {
      throw new NoSuchFileException(path.toString());
    }
    if (!Files.isReadable(path)) {
      throw new AccessDeniedException(path.toString());
    }
  }
}
