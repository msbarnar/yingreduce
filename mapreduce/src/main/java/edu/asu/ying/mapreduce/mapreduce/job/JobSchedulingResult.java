package edu.asu.ying.mapreduce.mapreduce.job;

import java.io.Serializable;

import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;

/**
 * Wraps the result of attempting to schedule a {@link Job} on a
 * {@link edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler}.
 */
public final class JobSchedulingResult implements Serializable {

  private static final long SerialVersionUID = 1L;

  public enum Result {
    Scheduled,
    DestinationNotFound,
    Exception
  }

  private final Job job;
  private final Result result;
  private final RemoteNode node;
  private Throwable cause = null;

  public JobSchedulingResult(final Job job, final RemoteNode node, final Result result) {
    this.job = job;
    this.node = node;
    this.result = result;
  }

  public JobSchedulingResult(final Job job, final RemoteNode node, final Throwable cause) {
    this.job = job;
    this.node = node;
    this.result = Result.Exception;
    this.cause = cause;
  }

  public final Job getJob() {
    return this.job;
  }

  public final Result getResult() {
    return this.result;
  }

  public final Throwable getCause() {
    return this.cause;
  }

  public final RemoteNode getNode() {
    return this.node;
  }
}
