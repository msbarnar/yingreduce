package edu.asu.ying.mapreduce.tasks.map;

import com.google.common.base.Preconditions;

import java.util.UUID;

import edu.asu.ying.mapreduce.tasks.InvalidTaskException;
import edu.asu.ying.mapreduce.tasks.ReducerReference;
import edu.asu.ying.mapreduce.tasks.Task;
import edu.asu.ying.mapreduce.tasks.TaskBase;

/**
 * A {@code MapTask} describes an instance of a task of mapping a function to a dataset.
 * </p>
 * The {@code MapTask} carries all of the information needed by a node to
 * <ol>
 *   <li>Forward the task to another node, if necessary</li>
 *   <li>Perform computations on a specific data set</li>
 *   <li>Forward the results to a "reducing" node</li>
 *   <li>Respond to state inquiries regarding the task</li>
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
  public MapTask(final String id) {
    this.setId(id);
  }
  public MapTask(final UUID id) {
    this(id.toString());
  }

  public void validate() throws InvalidTaskException {
    if (this.getReducer() == null) {
      throw new InvalidTaskException(this, "No reducing node set");
    }
  }

  public final ReducerReference getReducer() {
    return this.properties.getDynamicCast(Property.Reducer, ReducerReference.class);
  }
  public final void setReducer(final ReducerReference reducer) {
    Preconditions.checkNotNull(reducer);

    this.properties.put(Property.Reducer, reducer);
  }
}