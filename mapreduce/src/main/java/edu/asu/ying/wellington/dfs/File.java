package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.io.Writable;

/**
 * {@code File} is the base entry in the distributed filesystem.
 */
public final class File implements Writable {

  private static final long SerialVersionUID = 1L;

  public static FileProperties getDefaultProperties() {
    return new FileProperties();
  }

  private Path path;
  private FileProperties properties;

  public File(String path) throws InvalidPathException {
    this(new Path(path));
  }

  public File(String path, FileProperties properties) throws InvalidPathException {
    this(new Path(path), properties);
  }

  public File(Path path) {
    this(path, getDefaultProperties());
  }

  public File(Path path, FileProperties properties) {
    this.path = path;
    this.properties = new FileProperties(properties);
  }

  public Path getPath() {
    return path;
  }

  public FileProperties getProperties() {
    return properties;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.path = new Path();
    path.readFields(in);
    properties.readFields(in);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    path.write(out);
    properties.write(out);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || File.class != o.getClass()) && path.equals(((File) o).path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return "File{".concat(path.toString()).concat("}");
  }
}
