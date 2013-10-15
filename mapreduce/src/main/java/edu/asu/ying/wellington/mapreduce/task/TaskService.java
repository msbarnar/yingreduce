package edu.asu.ying.wellington.mapreduce.task;

import edu.asu.ying.wellington.mapreduce.Exported;
import edu.asu.ying.wellington.mapreduce.Service;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;

/**
 *
 */
public interface TaskService extends Service, Exported<RemoteTaskService> {

  void accept(Task task) throws TaskException;
}
