package edu.asu.ying.mapreduce.net;

import java.rmi.Remote;

/**
 * Provides an interface to a remote node via a {@link Remote} proxy.
 */
public interface RemoteNode extends Node, Remote {
}
