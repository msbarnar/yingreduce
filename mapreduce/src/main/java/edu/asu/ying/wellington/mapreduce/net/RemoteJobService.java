package edu.asu.ying.wellington.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activatable;
import edu.asu.ying.wellington.mapreduce.job.Job;

/**
 *
 */
public interface RemoteJobService extends Activatable {

  void accept(Job job) throws RemoteException;
}
