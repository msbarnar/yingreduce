package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.mapreduce.job.Job;

/**
 *
 */
public interface RemoteJobService extends Activatable {

  void accept(Job job) throws RemoteException;
}
