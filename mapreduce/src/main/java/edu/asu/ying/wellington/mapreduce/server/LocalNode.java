package edu.asu.ying.wellington.mapreduce.server;

import java.io.IOException;
import java.util.List;

import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 *
 */
public interface LocalNode {

  NodeIdentifier getId();

  RemoteNode getAsRemote();

  RemoteNode findNode(String searchKey) throws IOException;

  List<RemoteNode> findNodes(String searchKey, int count) throws IOException;

  List<RemoteNode> getNeighbors();

  JobService getJobService();

  TaskService getTaskService();

  DFSService getDFSService();
}
