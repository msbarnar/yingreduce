package edu.asu.ying.mapreduce.tasks;

import edu.asu.ying.mapreduce.common.Properties;

/**
 *
 */
public abstract class TaskBase implements Task {

  protected final static class Property {
    final static String TaskId = "task.id";
  }

  protected final Properties properties = new Properties();

  public Properties getProperties() {
    return this.properties;
  }

  public String getId() {
    return this.properties.getNullAsEmpty(Property.TaskId);
  }
}
