package edu.asu.ying.mapreduce.mapreduce.map;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.task.InvalidTaskException;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskBase;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.p2p.RemoteNode;
import edu.asu.ying.p2p.node.NodeURL;

/**
 * A {@code MapTask} describes an instance of a mapreduce of mapping a function to a dataset.
 * </p>
 * The {@code MapTask} carries all of the information needed by a node to
 * <ol>
 *   <li>Forward the mapreduce to another node, if necessary</li>
 *   <li>Perform computations on a specific table set</li>
 *   <li>Forward the results to a "reducing" node</li>
 *   <li>Respond to state inquiries regarding the mapreduce</li>
 * </ol>
 * Further, implementing {@link Task}, the {@code MapTask} carries with it a history of operations
 * performed by the nodes that handle it.
 */
public abstract class MapTask extends TaskBase {

  private final RemoteNode reductionNode;

  public MapTask(final Job parentJob, final RemoteNode reductionNode) {
    super(parentJob);
    this.reductionNode = reductionNode;
  }

  public abstract Serializable run();

  public final RemoteNode getReductionNode() {
    return this.reductionNode;
  }
}