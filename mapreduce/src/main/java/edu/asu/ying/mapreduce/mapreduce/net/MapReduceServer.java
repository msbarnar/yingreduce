package edu.asu.ying.mapreduce.mapreduce.net;

import edu.asu.ying.mapreduce.MapReduceService;
import edu.asu.ying.p2p.LocalPeer;

/**
 * {@code MapReduceServer} is the layer between the network and the {@link MapReduceService}.
 */
public final class MapReduceServer implements MapReduceService {

  private final LocalPeer networkPeer;

  public MapReduceServer(final LocalPeer networkPeer) {
    this.networkPeer = networkPeer;
  }

  @Override
  public JobService getJobService() {
    return new
  }

  @Override
  public DatabaseService getDatabaseService() {
    return null;
  }
}
