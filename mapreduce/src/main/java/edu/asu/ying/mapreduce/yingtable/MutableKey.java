package edu.asu.ying.mapreduce.yingtable;

/**
 *
 */
public final class MutableKey implements Element.Key {

  private final static long SerialVersionUID = 1L;

  private String row;
  private String column;

  public MutableKey() {
  }
  public MutableKey(final String row, final String column) {
    this.row = row;
    this.column = column;
  }

  public void setRow(final String row) { this.row = row; }
  @Override public String getRow() { return this.row; }

  public void setColumn(final String column) { this.column = column; }
  @Override public String getColumn() { return this.column; }
}
