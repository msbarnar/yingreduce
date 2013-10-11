package edu.asu.ying.wellington.mapreduce.job;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 */
public final class JobIdentifier implements Serializable, Comparable<JobIdentifier> {

  public static JobIdentifier random() {
    return new JobIdentifier(UUID.randomUUID().toString());
  }

  private static final long SerialVersionUID = 1L;

  private final String id;

  public JobIdentifier(String id) {
    this.id = Preconditions.checkNotNull(Strings.emptyToNull((id)));
  }

  @Override
  public int compareTo(JobIdentifier o) {
    return this.id.compareTo(o.id);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && this.id
        .equals(((JobIdentifier) o).id);
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
