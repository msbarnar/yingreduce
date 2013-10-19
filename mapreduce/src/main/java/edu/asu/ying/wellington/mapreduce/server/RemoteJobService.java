package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.mapreduce.job.Job;

/**
 *
 */
public interface RemoteJobService extends Activatable {

  /**
   * If the accepting node is the responsible node the job will be queued for delegation; otherwise
   * the job will be forwarded to the responsible node via the same interface.
   */
  void accept(Job job) throws RemoteException;
}
