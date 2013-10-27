package edu.asu.ying.mapreduce.task;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.mapreduce.server.RemoteTaskService;
import edu.asu.ying.wellington.Service;

/**
 *
 */
public interface TaskService extends Service, Exported<RemoteTaskService> {

  void accept(Task task) throws TaskException;

  int getBackpressure();
}
