package edu.asu.ying.wellington.mapreduce.job;

import java.util.UUID;

import edu.asu.ying.wellington.Identifier;

/**
 *
 */
public final class JobIdentifier extends Identifier {

  public static JobIdentifier random() {
    return new JobIdentifier(UUID.randomUUID().toString());
  }

  public static JobIdentifier forString(String id) {
    return new JobIdentifier(id);
  }

  private static final long SerialVersionUID = 1L;

  private static final String JOB_PREFIX = "job";

  private JobIdentifier(String id) {
    super(JOB_PREFIX, id);
  }
}
