package edu.asu.ying.wellington.mapreduce.job;

import edu.asu.ying.wellington.mapreduce.Exported;
import edu.asu.ying.wellington.mapreduce.Service;
import edu.asu.ying.wellington.mapreduce.server.RemoteJobService;

/**
 *
 */
public interface JobService extends Service, Exported<RemoteJobService> {

  void accept(Job job) throws JobException;
}
