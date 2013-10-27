package edu.asu.ying.mapreduce.job;

import edu.asu.ying.rmi.Exported;
import edu.asu.ying.mapreduce.server.RemoteJobService;
import edu.asu.ying.wellington.service.Service;

/**
 *
 */
public interface JobService extends Service, Exported<RemoteJobService> {

  void accept(Job job) throws JobException;
}
