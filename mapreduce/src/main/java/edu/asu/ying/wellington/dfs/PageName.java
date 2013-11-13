package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.AbstractIdentifier;
import edu.asu.ying.wellington.InvalidIdentifierException;

/**
 *
 */
public final class PageName extends AbstractIdentifier {

  private static final long serialVersionUID = 1L;

  public static PageName create(Path filePath, int index) {
    return new PageName(filePath, index);
  }

  public static PageName firstPageOf(Path path) {
    return new PageName(path, 0);
  }

  public static PageName forString(String name) throws InvalidPathException {
    int lastDelimiter = name.lastIndexOf(PAGE_DELIMITER);
    if (lastDelimiter > -1) {
      if (lastDelimiter == 0) {
        throw new InvalidIdentifierException("No file path in page identifier", name);
      }
      int pageIndex;
      try {
        pageIndex = Math.max(Integer.parseInt(name.substring(lastDelimiter + 1)), -1);
      } catch (NumberFormatException e) {
        throw new InvalidIdentifierException("Page index is not an integer", name);
      }
      return new PageName(new Path(name.substring(0, lastDelimiter)), pageIndex);
    } else {
      return firstPageOf(new Path(name));
    }
  }

  /**
   * Deserializes the identifier from {@code stream}.
   */
  public static PageName readFrom(DataInput input) throws IOException {
    PageName name = new PageName();
    name.readFields(input);
    return name;
  }

  private static final char PAGE_DELIMITER = '~';

  private Path filePath;
  private int index;

  private PageName() {
  }

  private PageName(Path filePath, int index) {
    super(filePath.toString()
              .concat(Character.toString(PAGE_DELIMITER))
              .concat(Integer.toString(index)));
    this.filePath = filePath;
    this.index = index;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.filePath = Path.readFrom(in);
    this.index = in.readInt();
    this.id = filePath.toString()
        .concat(Character.toString(PAGE_DELIMITER))
        .concat(Integer.toString(index));
  }

  @Override
  public void write(DataOutput out) throws IOException {
    filePath.write(out);
    out.writeInt(index);
  }

  public Path path() {
    return filePath;
  }

  public int index() {
    return index;
  }

  /**
   * Naturally compares by table name and then by index.
   * </p>
   * i.e. mytable~2 > mytable~1 > my~2 > my~1
   */
  public int compareTo(PageName o) {
    int pathComp = filePath.compareTo(o.filePath);
    if (pathComp != 0) {
      return pathComp;
    }

    return Integer.compare(index, o.index());
  }
}
