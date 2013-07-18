package edu.asu.ying.mapreduce.mapreduce.task;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.net.InetAddress;
import java.util.UUID;

import edu.asu.ying.mapreduce.common.Properties;

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
    static final String TaskId = "mapreduce.id";
    static final String TaskStartParameters = "mapreduce.parameters.start";
    static final String TaskHistory = "mapreduce.history";
    static final String ResponsibleNodeAddress = "mapreduce.nodes.responsible.address";
  }

  protected final Properties properties = new Properties();

  public Properties getProperties() {
    return this.properties;
  }

  public TaskID getId() {
    return this.properties.getDynamicCast(Property.TaskId, TaskID.class);
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

  public void setResponsibleNodeAddress(final InetAddress address) {
    this.properties.put(Property.ResponsibleNodeAddress, address);
  }
  public InetAddress getResponsibleNodeAddress() {
    return this.properties.getDynamicCast(Property.ResponsibleNodeAddress, InetAddress.class);
  }

  public boolean isCurrentlyAtInitialNode() {
    final TaskHistory.Entry lastEntry = this.getHistory().last();
    if (lastEntry == null) {
      // The responsible node didn't append itself to the history before distributing this mapreduce from
      // the original job.
      throw new IllegalStateException("Scheduler received mapreduce with no responsible node set; don't"
                                      + " know whether this is the initial node, and won't know the"
                                      + " origin of the mapreduce if we continue forwarding.");
    }
    return lastEntry.getNodeRole() == TaskHistory.NodeRole.Responsible;
  }
}
