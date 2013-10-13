package edu.asu.ying.wellington.dfs.table;

import java.util.UUID;

import edu.asu.ying.wellington.Identifier;
import edu.asu.ying.wellington.InvalidIdentifierException;

/**
 *
 */
public final class TableIdentifier extends Identifier {

  private static final char PAGE_DELIMITER = '~';

  public static TableIdentifier random() {
    return new TableIdentifier(UUID.randomUUID().toString());
  }

  public static TableIdentifier forString(String id) {
    int lastDelimiter = id.lastIndexOf(PAGE_DELIMITER);
    if (lastDelimiter > -1) {
      if (lastDelimiter == 0) {
        throw new InvalidIdentifierException("No table name in table identifier", id);
      }
      int pageIndex = NO_PAGE;
      try {
        pageIndex = Math.max(Integer.parseInt(id.substring(lastDelimiter + 1)), NO_PAGE);
      } catch (NumberFormatException e) {
        throw new InvalidIdentifierException("Page index is not an integer", id);
      }
      return new TableIdentifier(id.substring(0, lastDelimiter), pageIndex);
    }

    return new TableIdentifier(id);
  }

  public static final int NO_PAGE = -1;

  private static final long SerialVersionUID = 1L;
  private static final String TABLE_PREFIX = "tab";

  private final String tableName;
  private final int page;

  private TableIdentifier(String tableName) {
    this(tableName, NO_PAGE);
  }

  private TableIdentifier(String tableName, int page) {
    super(TABLE_PREFIX, tableName.concat(Character.toString(PAGE_DELIMITER)).concat(
        Integer.toString(page)));
    this.tableName = tableName;
    this.page = page;
  }

  public TableIdentifier forPage(int index) throws IndexOutOfBoundsException {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Page index must be 0 or a positive integer.");
    }
    return new TableIdentifier(this.tableName, index);
  }

  public String getTableName() {
    return this.tableName;
  }

  public int getPageIndex() {
    return this.page;
  }

  public boolean isPageSpecified() {
    return this.page > NO_PAGE;
  }
}
