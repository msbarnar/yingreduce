package edu.asu.ying.mapreduce.mapreduce.net;

/**
 *
 */
public interface LocalNode {

  NodeIdentifier getNodeID();

  RemoteNode findNode(String searchKey);
}
