package edu.asu.ying.wellington.mapreduce.job;

import java.util.UUID;

import edu.asu.ying.wellington.AbstractIdentifier;

/**
 *
 */
public final class JobIdentifier extends AbstractIdentifier {

  public static JobIdentifier random() {
    return new JobIdentifier(UUID.randomUUID().toString());
  }

  public static JobIdentifier forString(String id) {
    return new JobIdentifier(id);
  }

  private static final long SerialVersionUID = 1L;

  private JobIdentifier(String id) {
    super(id);
  }
}
