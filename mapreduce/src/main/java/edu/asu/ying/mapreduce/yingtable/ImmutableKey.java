package edu.asu.ying.mapreduce.yingtable;

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

  @Override public String getRow() { return this.row; }
  @Override public String getColumn() { return this.column; }
}
