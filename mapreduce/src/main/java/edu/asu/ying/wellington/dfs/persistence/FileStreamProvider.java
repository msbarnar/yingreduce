package edu.asu.ying.wellington.dfs.persistence;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public final class FileStreamProvider implements StreamProvider {

  private final Path root;

  @Inject
  private FileStreamProvider(@Named("dfs.store.path") String rootPath) throws IOException {
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

  public OutputStream getOutputStream(String path) throws IOException {
    Path fullPath = root.resolve(path);
    Files.deleteIfExists(fullPath);
    File file = fullPath.toFile();
    com.google.common.io.Files.createParentDirs(file);
    // Overwrite existing file
    return new BufferedOutputStream(new FileOutputStream(file));
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
