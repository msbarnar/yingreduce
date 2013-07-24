package edu.asu.ying.mapreduce.rmi.remote;

import java.rmi.Remote;

import edu.asu.ying.mapreduce.net.Node;

/**
 * Provides an interface to a remote node via a {@link Remote} proxy.
 */
public interface NodeProxy extends Node, Remote {
}
