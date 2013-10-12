package edu.asu.ying.wellington.mapreduce.net;

import java.util.List;

import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 *
 */
public interface LocalNode {

  NodeIdentifier getIdentifier();

  RemoteNode getAsRemote();

  RemoteNode findNode(String searchKey);

  List<RemoteNode> findNodes(String searchKey);

  JobService getJobService();

  TaskService getTaskService();
}
