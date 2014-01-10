package edu.asu.ying.wellington;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.mapreduce.server.RemoteReducer;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public interface LocalNode extends Exported<RemoteNode> {

  String getName();

  void stop();

  RemoteReducer getReducerFor(Task task);
}
