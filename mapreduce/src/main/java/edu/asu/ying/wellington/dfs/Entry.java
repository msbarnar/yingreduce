package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class Entry<C extends WritableComparable,
    R extends WritableComparable,
    V extends Writable>
    implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final Key<C, R> key;
  private final V value;

  public Entry(Key<C, R> key, V value) {
    this.key = key;
    this.value = value;
  }

  public Entry(C column, R row, V value) {
    this.key = new Key<>(column, row);
    this.value = value;
  }

  public final Key<C, R> getKey() {
    return key;
  }

  public final V getValue() {
    return value;
  }

  public final class Key<C extends WritableComparable, R extends WritableComparable>
      implements WritableComparable<Key<C, R>> {

    private static final long SerialVersionUID = 1L;

    private final C column;
    private final R row;

    public Key(C column, R row) {
      this.row = row;
      this.column = column;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Key<C, R> o) {
      int colComp = column.compareTo(o.getColumn());
      if (colComp != 0) {
        return colComp;
      }
      return row.compareTo(o.getRow());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
      column.readFields(in);
      row.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
      column.write(out);
      row.write(out);
    }

    public R getRow() {
      return row;
    }

    public C getColumn() {
      return column;
    }
  }
}
