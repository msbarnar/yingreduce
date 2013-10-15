package edu.asu.ying.wellington.mapreduce.server;

import edu.asu.ying.wellington.mapreduce.Exported;

/**
 *
 */
public interface LocalNode extends Exported<RemoteNode> {

  NodeIdentifier getID();
}
