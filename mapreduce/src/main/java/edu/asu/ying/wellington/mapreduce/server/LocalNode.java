package edu.asu.ying.wellington.mapreduce.server;

/**
 *
 */
public interface LocalNode {

  NodeIdentifier getID();

  RemoteNode getAsRemote();
}
