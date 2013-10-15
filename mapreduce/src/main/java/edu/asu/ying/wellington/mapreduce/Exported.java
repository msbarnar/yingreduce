package edu.asu.ying.wellington.mapreduce;

import edu.asu.ying.p2p.rmi.Activatable;

/**
 *
 */
public interface Exported<R extends Activatable> {

  R asRemote();
}
