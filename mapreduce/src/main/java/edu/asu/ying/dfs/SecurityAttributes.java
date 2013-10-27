package edu.asu.ying.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.io.Writable;

/**
 *
 */
public class SecurityAttributes implements Serializable, Writable {

  private static final long SerialVersionUID = 1L;

  private static final String PROPERTIES_KEY = ".security";

  public static SecurityAttributes get(FileProperties properties) {
    return new SecurityAttributes(properties.get(PROPERTIES_KEY));
  }

  public SecurityAttributes() {
    setDefaults();
  }

  /**
   * Copy constructor
   */
  private SecurityAttributes(Object attributes) {
    if ((attributes instanceof SecurityAttributes)) {
      setDefaults();
    }
    // TODO: Copy
  }

  private void setDefaults() {
  }

  @Override
  public void readFields(DataInput in) throws IOException {
  }

  @Override
  public void write(DataOutput out) throws IOException {
  }
}
