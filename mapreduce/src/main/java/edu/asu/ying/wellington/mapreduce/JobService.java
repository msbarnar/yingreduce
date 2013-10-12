package edu.asu.ying.wellington.mapreduce;

import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobException;

/**
 *
 */
public interface JobService {

  void accept(Job job) throws JobException;
}
