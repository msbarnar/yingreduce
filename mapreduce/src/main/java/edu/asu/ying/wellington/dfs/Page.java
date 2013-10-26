package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.io.Writable;

/**
 * A {@code Page} is a chunk of predefined size of a {@link File}'s contents.
 */
public class Page implements Writable {

  public static Page readFrom(DataInput in) throws IOException {
    Page p = new Page();
    p.readFields(in);
    return p;
  }

  private File file;
  private PageName name;

  private Page() {
  }

  public Page(File file, int index) {
    this.file = file;
    this.name = PageName.create(file.getPath(), index);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    file = File.readFrom(in);
    name = PageName.readFrom(in);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    file.write(out);
    name.write(out);
  }
}
