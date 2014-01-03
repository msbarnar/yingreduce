package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableInt;

/**
 * A {@code Page} is a chunk of predefined size of a {@link File}'s contents.
 */
public class Page implements Writable {

  public static Page firstPageOf(File file) {
    return new Page(file, 0);
  }

  public static Page readFrom(DataInput in) throws IOException {
    Page p = new Page();
    p.readFields(in);
    return p;
  }

  private File file;
  private PageName name;
  // Capacity is stored as a file property, but keep here for speedy reading
  private transient int capacity;
  private int size;

  private Page() {
  }

  /**
   * Creates the {@code index}th page of {@code file}.
   * </p>
   * The capacity of the page is read from the file properties and <b>must</b> be set before pages
   * can be created.
   */
  public Page(File file, int index) {
    this.file = file;
    this.name = PageName.create(file.path(), index);
    // Read the capacity from the file properties
    readCapacity();
    if (capacity <= 0) {
      throw new IllegalArgumentException("Page capacity not set: ".concat(file.toString()));
    }
  }

  public Page next() {
    return new Page(file, name.index() + 1);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    file = File.readFrom(in);
    name = PageName.readFrom(in);
    size = in.readInt();
    // Cache the capacity from the file properties
    readCapacity();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    file.write(out);
    name.write(out);
    out.writeInt(size);
  }

  public PageName name() {
    return name;
  }

  public File file() {
    return file;
  }

  /**
   * Returns the number of bytes in this page.
   */
  public int size() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  /**
   * Returns the maximum capacity in bytes of pages of this file, or -1 if not set.
   */
  public int capacity() {
    return capacity;
  }

  /**
   * Reads the page capacity from the file properties.
   */
  private void readCapacity() {
    try {
      capacity = ((WritableInt) file.properties()
          .get(File.Properties.PageCapacity.toString()))
          .get();
    } catch (NullPointerException | ClassCastException e) {
      capacity = -1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Page page = (Page) o;

    return capacity == page.capacity && size == page.size && file.equals(page.file) && name
        .equals(page.name);

  }

  @Override
  public int hashCode() {
    int result = file.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + capacity;
    result = 31 * result + size;
    return result;
  }

  @Override
  public String toString() {
    return "Page{".concat(name.toString()).concat("}");
  }
}
