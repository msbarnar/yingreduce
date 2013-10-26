package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.AbstractIdentifier;
import edu.asu.ying.wellington.InvalidIdentifierException;
import edu.asu.ying.wellington.io.WritableString;

/**
 *
 */
public final class PageIdentifier extends AbstractIdentifier {

  private static final long SerialVersionUID = 1L;

  public static PageIdentifier create(String tableName, int index) {
    return new PageIdentifier(tableName, index);
  }

  public static PageIdentifier firstPageOf(String tableName) {
    return new PageIdentifier(tableName, 0);
  }

  public static PageIdentifier forString(String id) {
    int lastDelimiter = id.lastIndexOf(PAGE_DELIMITER);
    if (lastDelimiter > -1) {
      if (lastDelimiter == 0) {
        throw new InvalidIdentifierException("No table name in page identifier", id);
      }
      int pageIndex;
      try {
        pageIndex = Math.max(Integer.parseInt(id.substring(lastDelimiter + 1)), -1);
      } catch (NumberFormatException e) {
        throw new InvalidIdentifierException("PageMetadata index is not an integer", id);
      }
      return new PageIdentifier(id.substring(0, lastDelimiter), pageIndex);
    } else {
      throw new InvalidIdentifierException("PageMetadata index not specified", id);
    }
  }

  /**
   * Deserializes the identifier from {@code stream}.
   */
  public static PageIdentifier readFrom(InputStream stream) throws IOException {
    DataInputStream istream;
    if (stream instanceof DataInputStream) {
      istream = (DataInputStream) stream;
    } else {
      istream = new DataInputStream(stream);
    }
    PageIdentifier id = new PageIdentifier();
    id.readFields(istream);
    return id;
  }

  private static final char PAGE_DELIMITER = '~';

  private String tableName;
  private int index;

  private PageIdentifier() {
  }

  private PageIdentifier(String tableName, int index) {
    super(tableName
              .concat(Character.toString(PAGE_DELIMITER))
              .concat(Integer.toString(index)));
    this.tableName = tableName;
    this.index = index;
  }

  public String getTableName() {
    return tableName;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    WritableString table = new WritableString();
    table.readFields(in);
    this.tableName = table.toString();
    this.index = in.readInt();
    this.id = tableName
        .concat(Character.toString(PAGE_DELIMITER))
        .concat(Integer.toString(index));
  }

  @Override
  public void write(DataOutput out) throws IOException {
    (new WritableString(tableName)).write(out);
    out.writeInt(index);
  }

  /**
   * Naturally compares by table name and then by index.
   * </p>
   * i.e. mytable~2 > mytable~1 > my~2 > my~1
   */
  public int compareTo(PageIdentifier o) {
    int tableComp = tableName.compareTo(o.getTableName());
    if (tableComp != 0) {
      return tableComp;
    }

    return Integer.compare(index, o.getIndex());
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
