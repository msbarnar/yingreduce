package edu.asu.ying.mapreduce.tasks;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.UUID;

import edu.asu.ying.mapreduce.common.Properties;

/**
 * {@code TaskBase} is the base class of all distributable tasks.
 * </p>
 * Properties defined by this class are:
 * <ul>
 *   <il>{@code task.id} - the universally unique ID of the task</il>
 * </ul>
 */
public abstract class TaskBase implements Task {

  protected static final class Property {
    static final String TaskId = "task.id";
    static final String TaskStartParameters = "task.parameters.start";
  }

  protected final Properties properties = new Properties();

  public Properties getProperties() {
    return this.properties;
  }

  public String getId() {
    return this.properties.getNullAsEmpty(Property.TaskId);
  }
  /**
   * Sets a random universally unique identifier.
   */
  protected void setId() {
    this.setId(UUID.randomUUID().toString());
  }
  protected void setId(final UUID uuid) {
    this.setId(uuid.toString());
  }
  protected void setId(final String id) {
    Preconditions.checkNotNull(Strings.emptyToNull(id));

    this.properties.put(Property.TaskId, id);
  }

  /**
   * The {@code TaskStartParameters} define the timing of the task's starting.
   * @return the task's start parameters, or {@link TaskStartParameters#Default} if they are not
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
   * Sets the task's start parameters, or {@link TaskStartParameters#Default} if {@code params} is
   * null.
   */
  protected void setTaskStartParameters(TaskStartParameters params) {
    if (params == null) {
      params = TaskStartParameters.Default;
    }
    this.properties.put(Property.TaskStartParameters, params);
  }
}
