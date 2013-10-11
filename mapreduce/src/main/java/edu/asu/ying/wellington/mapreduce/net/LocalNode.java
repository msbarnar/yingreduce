package edu.asu.ying.wellington.mapreduce.net;

/**
 *
 */
public interface LocalNode {

  NodeIdentifier getNodeID();

  RemoteNode findNode(String searchKey);
}
