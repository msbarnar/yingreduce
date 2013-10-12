package edu.asu.ying.wellington.mapreduce.task;

import edu.asu.ying.wellington.mapreduce.Service;
import edu.asu.ying.wellington.mapreduce.net.RemoteTaskService;

/**
 *
 */
public interface TaskService extends Service<RemoteTaskService> {

  void accept(Task task) throws TaskException;

  Class<? extends RemoteTaskService> getWrapper();
}
