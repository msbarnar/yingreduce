package edu.asu.ying.mapreduce.net;

import java.rmi.Remote;

import edu.asu.ying.mapreduce.task.scheduling.Scheduler;

/**
 * Provides an interface to a remote node via a {@link Remote} proxy.
 */
public interface RemoteNode extends Node, Remote {
}
