package edu.asu.ying.mapreduce.node.rmi;

import java.io.Serializable;
import java.rmi.Remote;

import edu.asu.ying.mapreduce.node.Node;

/**
 * Provides an interface to a remote node via a {@link Remote} proxy.
 */
public interface RemoteNodeProxy extends Node, Remote, Serializable {
}
