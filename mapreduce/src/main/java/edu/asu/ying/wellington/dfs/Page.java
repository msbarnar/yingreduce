package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.io.Writable;

/**
 * A {@code Page} is a chunk of predefined size of a {@link File}'s contents.
 */
public class Page implements Writable {

  private final File file;
  private final PageName id;

  public Page(int index)

  @Override
  public void readFields(DataInput in) throws IOException {
  }

  @Override
  public void write(DataOutput out) throws IOException {
  }
}
