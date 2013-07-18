package edu.asu.ying.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.net.messaging.activator.ActivatorRequest;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.rmi.ActivatorNotFoundException;
import edu.asu.ying.mapreduce.table.Table;
import edu.asu.ying.mapreduce.table.TableID;
import edu.asu.ying.mapreduce.task.scheduling.Scheduler;

/**
 *
 */
public class RemoteNodeImpl implements RemoteNode {

  @Override
  public Scheduler getScheduler() {
    return null;
  }

  @Override
  public Table getTable(TableID id) {
    return null;
  }

  @Override
  public NodeURI getNodeURI() {
    return null;
  }

  /**
   * Sends an {@link ActivatorRequest} to the remote node and waits for a response, blocking until
   * one arrives.
   * @return A {@link java.rmi.Remote} reference to an {@link Activator} on the remote node.
   */
  private Activator getRemoteActivator() throws ActivatorNotFoundException {
    final ActivatorRequest request = new ActivatorRequest(this.getNodeURI());
  }
}
