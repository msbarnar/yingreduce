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
public abstract class AbstractIdentifier implements WritableComparable<AbstractIdentifier> {

  protected String id;

  protected AbstractIdentifier() {
  }

  protected AbstractIdentifier(String id) {
    this.id = Preconditions.checkNotNull(Strings.emptyToNull((id)));
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.id = in.readUTF();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(this.id);
  }

  @Override
  public int compareTo(AbstractIdentifier o) {
    return this.id.compareTo(o.id);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && this.id
        .equals(((AbstractIdentifier) o).id);
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
