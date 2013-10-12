package edu.asu.ying.wellington.mapreduce.net;

import java.util.List;

import edu.asu.ying.wellington.mapreduce.JobService;
import edu.asu.ying.wellington.mapreduce.TaskService;

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
