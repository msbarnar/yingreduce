package edu.asu.ying.mapreduce.mapreduce.map;

import com.google.common.base.Preconditions;

import edu.asu.ying.mapreduce.mapreduce.task.InvalidTaskException;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskBase;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.mapreduce.node.NodeURL;

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
public final class MapTask extends TaskBase {

  private static final class Property {
    static final String Reducer = "reducer";
  }

  public MapTask() {
    this.setId();
  }
  public MapTask(final TaskID id) {
    this.setId(id);
  }

  public void validate() throws InvalidTaskException {
    if (this.getReducer() == null) {
      throw new InvalidTaskException(this, "No reducing node set");
    }
  }

  public final NodeURL getReducer() {
    return this.properties.getDynamicCast(Property.Reducer, NodeURL.class);
  }
  public final void setReducer(final NodeURL reducer) {
    Preconditions.checkNotNull(reducer);

    this.properties.put(Property.Reducer, reducer);
  }
}