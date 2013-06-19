package edu.asu.ying.mapreduce.tasks.map;

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

}