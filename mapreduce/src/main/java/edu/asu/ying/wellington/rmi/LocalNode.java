package edu.asu.ying.wellington.rmi;

import edu.asu.ying.common.remoting.Exported;

/**
 *
 */
public interface LocalNode extends Exported<RemoteNode> {

  String getName();
}
