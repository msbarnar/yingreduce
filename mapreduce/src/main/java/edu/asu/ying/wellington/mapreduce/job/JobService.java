package edu.asu.ying.wellington.mapreduce.job;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.Service;
import edu.asu.ying.wellington.mapreduce.server.RemoteJobService;

/**
 *
 */
public interface JobService extends Service, Exported<RemoteJobService> {

  void accept(Job job) throws JobException;
}
