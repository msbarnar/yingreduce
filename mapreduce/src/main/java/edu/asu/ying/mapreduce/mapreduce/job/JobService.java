package edu.asu.ying.mapreduce.mapreduce.job;

/**
 *
 */
public interface JobService {

  void accept(Job job) throws JobException;
}
