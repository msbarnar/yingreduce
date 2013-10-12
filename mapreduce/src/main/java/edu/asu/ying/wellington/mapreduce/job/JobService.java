package edu.asu.ying.wellington.mapreduce.job;

/**
 *
 */
public interface JobService {

  void accept(Job job) throws JobException;
}
