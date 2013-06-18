package edu.asu.ying.mapreduce.tasks;

import edu.asu.ying.mapreduce.common.Properties;

/**
 * A {@code Task} is the basic unit of work in the map/reduce system.
 * </p>
 * Tasks are how nodes communicate pending or completed work.
 */
public interface Task {

  Properties getProperties();
}
