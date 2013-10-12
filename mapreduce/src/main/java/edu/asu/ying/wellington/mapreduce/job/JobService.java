package edu.asu.ying.wellington.mapreduce.job;

import edu.asu.ying.wellington.mapreduce.Service;
import edu.asu.ying.wellington.mapreduce.net.RemoteJobService;

/**
 *
 */
public interface JobService extends Service<RemoteJobService> {

  void accept(Job job) throws JobException;
}
