package edu.asu.ying.wellington.mapreduce.task;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.Service;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;

/**
 *
 */
public interface TaskService extends Service, Exported<RemoteTaskService> {

  void accept(Task task) throws TaskException;

  int getBackpressure();
}
