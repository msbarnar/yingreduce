package edu.asu.ying.wellington.dfs;

import java.io.Serializable;
import java.nio.file.InvalidPathException;
import java.util.Properties;

/**
 * {@code File} is the base entry in the distributed filesystem.
 */
public final class File implements Serializable {

  private static final long SerialVersionUID = 1L;

  public static Properties getDefaultProperties() {
    return new Properties();
  }

  private final String path;
  private final String name;
  private final Properties properties = getDefaultProperties();

  public File(String path) {
    parsePath(path);
  }

  public File(String path, Properties properties) {
    parsePath(path);
    this.properties.putAll(properties);
  }

  public String getName() {
    return name;
  }

  public String getPath() {

  }

  private void parsePath(String path) throws InvalidPathException {
    if (!path.startsWith(PATH_DELIMITER)) {
      path = PATH_DELIMITER.concat(path);
    }
  }
}
