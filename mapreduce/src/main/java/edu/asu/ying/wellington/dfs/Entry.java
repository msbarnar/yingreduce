package edu.asu.ying.wellington.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class Entry<R extends WritableComparable,
    C extends WritableComparable,
    V extends WritableComparable>
    implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final Key<R, C> key;
  private final V value;

  public Entry(Key<R, C> key, V value) {
    this.key = key;
    this.value = value;
  }

  public final Key<R, C> getKey() {
    return key;
  }

  public final V getValue() {
    return value;
  }

  public final class Key<R extends WritableComparable, C extends WritableComparable>
      implements WritableComparable<Key<R, C>> {

    private static final long SerialVersionUID = 1L;

    private final R row;
    private final C column;

    public Key(R row, C column) {
      this.row = row;
      this.column = column;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Key<R, C> o) {
      int colComp = column.compareTo(o.getColumn());
      if (colComp != 0) {
        return colComp;
      }
      return row.compareTo(o.getRow());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
      row.readFields(in);
      column.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
      row.write(out);
      column.write(out);
    }

    public R getRow() {
      return row;
    }

    public C getColumn() {
      return column;
    }
  }
}
