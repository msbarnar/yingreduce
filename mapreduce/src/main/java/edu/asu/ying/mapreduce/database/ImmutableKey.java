package edu.asu.ying.mapreduce.database;

import edu.asu.ying.mapreduce.database.element.Element;

/**
 *
 */
public final class ImmutableKey implements Element.Key {

  private final static long SerialVersionUID = 1L;

  private final String row;
  private final String column;

  public ImmutableKey(final String row, final String column) {
    this.row = row;
    this.column = column;
  }

  public ImmutableKey(final Element.Key copy) {
    this.row = copy.getRow();
    this.column = copy.getColumn();
  }

  @Override public String getRow() { return this.row; }
  @Override public String getColumn() { return this.column; }
}
