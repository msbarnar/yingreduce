package edu.asu.ying.wellington.dfs.persistence;

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

  @Override
  public OutputStream getOutputStream(String path) throws IOException {
    Path fullPath = root.resolve(path);
    Files.deleteIfExists(fullPath);
    File file = fullPath.toFile();
    com.google.common.io.Files.createParentDirs(file);
    // Overwrite existing file
    return new BufferedOutputStream(new FileOutputStream(file));
  }

  @Override
  public InputStream getInputStream(String path) throws IOException {
    Path fullPath = root.resolve(path);
    if (!Files.exists(fullPath)) {
      throw new NoSuchFileException(fullPath.toString());
    }
    if (!Files.isReadable(fullPath)) {
      throw new AccessDeniedException(fullPath.toString());
    }

    return new BufferedInputStream(new FileInputStream(fullPath.toFile()));
  }
}
