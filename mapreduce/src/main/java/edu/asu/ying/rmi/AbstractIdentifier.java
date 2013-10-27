package edu.asu.ying.rmi;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.io.WritableComparable;

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
    out.writeUTF(id);
  }

  @Override
  public int compareTo(AbstractIdentifier o) {
    return id.compareTo(o.id);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && id
        .equals(((AbstractIdentifier) o).id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return id;
  }
}
