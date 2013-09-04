package edu.asu.ying.mapreduce.mapreduce.job;

import java.io.Serializable;

import edu.asu.ying.p2p.NodeIdentifier;

/**
 * Wraps the result of attempting to schedule a {@link Job} on a
 * {@link edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler}.
 */
public final class JobSchedulingResult implements Serializable {

  private static final long SerialVersionUID = 1L;

  public enum Result {
    Scheduled,
    DestinationNotFound
  }

  private final Job job;
  private Result result;
  private final NodeIdentifier nodeIdentifier;

  public JobSchedulingResult(final Job job, final NodeIdentifier nodeIdentifier) {
    this.job = job;
    this.nodeIdentifier = nodeIdentifier;
  }

  public final JobSchedulingResult setResult(final Result result) {
    this.result = result;
    return this;
  }

  public final Job getJob() {
    return this.job;
  }

  public final Result getResult() {
    return this.result;
  }

  public final NodeIdentifier getNodeIdentifier() {
    return this.nodeIdentifier;
  }
}
