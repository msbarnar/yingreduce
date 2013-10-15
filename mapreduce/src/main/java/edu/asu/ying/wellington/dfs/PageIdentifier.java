package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.AbstractIdentifier;
import edu.asu.ying.wellington.InvalidIdentifierException;

/**
 *
 */
public final class PageIdentifier extends AbstractIdentifier {

  private static final long SerialVersionUID = 1L;

  public static PageIdentifier create(TableIdentifier parentTable, int index) {
    return new PageIdentifier(parentTable, index);
  }

  public static PageIdentifier firstPageOf(TableIdentifier table) {
    return new PageIdentifier(table, 0);
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
        throw new InvalidIdentifierException("Page index is not an integer", id);
      }
      return new PageIdentifier(TableIdentifier.forString(id.substring(0, lastDelimiter)),
                                pageIndex);
    } else {
      throw new InvalidIdentifierException("Page index not specified", id);
    }
  }

  private static final char PAGE_DELIMITER = '~';

  private final TableIdentifier table;
  private int index;

  private PageIdentifier(TableIdentifier table, int index) {
    super(table.toString()
              .concat(Character.toString(PAGE_DELIMITER))
              .concat(Integer.toString(index)));
    this.table = table;
    this.index = index;
  }

  public TableIdentifier getTableID() {
    return table;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    table.readFields(in);
    index = in.readInt();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    table.write(out);
    out.writeInt(index);
  }

  public int compareTo(PageIdentifier o) {
    int tableComp = table.compareTo(o.getTableID());
    if (tableComp != 0) {
      return tableComp;
    }

    return Integer.compare(index, o.getIndex());
  }
}
