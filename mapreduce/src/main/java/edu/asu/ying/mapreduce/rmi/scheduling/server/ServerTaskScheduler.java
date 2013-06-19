package edu.asu.ying.mapreduce.rmi.scheduling.server;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.rmi.scheduling.Scheduler;

/**
 *
 */
public class ServerTaskScheduler implements Scheduler {

  @Override
  public String getString() throws RemoteException {
    return "Hello! This is only a test.";
  }
}
