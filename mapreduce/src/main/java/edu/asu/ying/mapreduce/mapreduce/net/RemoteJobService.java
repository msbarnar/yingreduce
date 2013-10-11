package edu.asu.ying.mapreduce.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.p2p.rmi.Activatable;

/**
 *
 */
public interface RemoteJobService extends Activatable {

  void accept(Job job) throws RemoteException;
}
