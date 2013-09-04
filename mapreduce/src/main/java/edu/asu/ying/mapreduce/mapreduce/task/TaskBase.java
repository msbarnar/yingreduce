package edu.asu.ying.mapreduce.mapreduce.task;

import com.google.common.base.Preconditions;

import java.net.InetAddress;

import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;

/**
 * {@code TaskBase} is the base class of all distributable mapreduce.
 * </p>
 * Properties defined by this class are:
 * <ul>
 *   <il>{@code mapreduce.id} - the universally unique ID of the mapreduce</il>
 * </ul>
 */
public abstract class TaskBase implements Task {

  private static final long SerialVersionUID = 1L;

  protected static final class Property {
    static final String TaskId = "task.id";
    static final String TaskStartParameters = "task.parameters.start";
    static final String TaskHistory = "task.history";
    static final String Job = "job";
    static final String InitialNode = "task.initial-node";
  }

  protected final Properties properties = new Properties();

  public Properties getProperties() {
    return this.properties;
  }

  public TaskID getId() {
    final TaskID id = this.properties.getDynamicCast(Property.TaskId, TaskID.class);
    if (id == null) {
      this.setId();
      return this.getId();
    }
    return id;
  }
  /**
   * Sets a random universally unique identifier.
   */
  protected void setId() {
    this.setId(new TaskID());
  }
  protected void setId(final TaskID id) {
    Preconditions.checkNotNull(id);

    this.properties.put(Property.TaskId, id);
  }

  /**
   * The {@code TaskStartParameters} define the timing of the mapreduce's starting.
   * @return the mapreduce's start parameters, or {@link TaskStartParameters#Default} if they are not
   * set.
   */
  public TaskStartParameters getTaskStartParameters() {
    TaskStartParameters params = this.properties.getDynamicCast(Property.TaskStartParameters,
                                                                TaskStartParameters.class);
    if (params == null) {
      params = TaskStartParameters.Default;
      this.setTaskStartParameters(params);
    }

    return params;
  }

  /**
   * Sets the mapreduce's start parameters, or {@link TaskStartParameters#Default} if {@code params} is
   * null.
   */
  protected void setTaskStartParameters(TaskStartParameters params) {
    if (params == null) {
      params = TaskStartParameters.Default;
    }
    this.properties.put(Property.TaskStartParameters, params);
  }

  protected void setTaskHistory(final TaskHistory history) {
    this.properties.put(Property.TaskHistory, history);
  }

  /**
   * The mapreduce's history is a log of the schedulers that have visited the mapreduce and the actions they
   * have performed.
   */
  public TaskHistory getHistory() {
    TaskHistory history = this.properties.getDynamicCast(Property.TaskHistory, TaskHistory.class);
    if (history == null) {
      history = new TaskHistory();
      this.setTaskHistory(history);
    }
    return history;
  }

  public void setInitialNode(final RemoteNode node) {
    this.properties.put(Property.InitialNode, node);
  }
  public RemoteNode getInitialNode() {
    return this.properties.getDynamicCast(Property.InitialNode, RemoteNode.class);
  }
}
