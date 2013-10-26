package edu.asu.ying.wellington.dfs;

import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nullable;

import edu.asu.ying.wellington.io.Writable;

/**
 * {@code File} is the base entry in the distributed filesystem.
 */
public final class File implements Writable {

  public static enum Properties {

    PageCapacity(".page.capacity"),;

    private final String key;

    private Properties(String key) {
      this.key = key;
    }

    @Override
    public String toString() {
      return key;
    }
  }

  private static final long SerialVersionUID = 1L;

  public static FileProperties getDefaultProperties() {
    return new FileProperties();
  }

  public static File readFrom(DataInput in) throws IOException {
    File f = new File();
    f.readFields(in);
    return f;
  }

  private Path path;
  private FileProperties properties;

  private File() {
  }

  public File(String path) throws InvalidPathException {
    this(new Path(path));
  }

  public File(String path, @Nullable FileProperties properties) throws InvalidPathException {
    this(new Path(path), properties);
  }

  public File(Path path) {
    this(path, getDefaultProperties());
  }

  public File(Path path, @Nullable FileProperties properties) {
    this.path = Preconditions.checkNotNull(path);
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
    path = Path.readFrom(in);
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

  public enum OutputMode {
    CreateNew,
    Overwrite,
    Append
  }
}
