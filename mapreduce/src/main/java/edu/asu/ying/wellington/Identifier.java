package edu.asu.ying.wellington;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public abstract class Identifier implements WritableComparable<Identifier> {

  private static final char SEPARATOR = '_';

  protected String prefix;
  protected String id;

  protected Identifier() {
  }

  protected Identifier(String prefix, String id) {
    this.prefix = Preconditions.checkNotNull(Strings.emptyToNull(prefix));
    this.id = (this.prefix + SEPARATOR)
        .concat(Preconditions.checkNotNull(Strings.emptyToNull((id))));
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.id = in.readUTF();
    int prefixIndex = this.id.indexOf(SEPARATOR);
    if (prefixIndex > 0) {
      this.prefix = this.id.substring(0, prefixIndex);
    }
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(this.id);
  }

  @Override
  public int compareTo(Identifier o) {
    return this.id.compareTo(o.id);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && this.id
        .equals(((Identifier) o).id);
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return this.id;
  }
}
