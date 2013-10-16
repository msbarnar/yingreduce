package edu.asu.ying.wellington.mapreduce.server;

import edu.asu.ying.common.remoting.Exported;

/**
 *
 */
public interface LocalNode extends Exported<RemoteNode> {

  String getName();
}
