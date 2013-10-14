package edu.asu.ying.wellington.mapreduce.job;

import edu.asu.ying.wellington.mapreduce.Service;

/**
 *
 */
public interface JobService extends Service {

  void accept(Job job) throws JobException;
}
