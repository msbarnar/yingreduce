package edu.asu.ying.mapreduce.job;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.mapreduce.server.RemoteJobService;
import edu.asu.ying.wellington.Service;

/**
 *
 */
public interface JobService extends Service, Exported<RemoteJobService> {

  void accept(Job job) throws JobException;
}
