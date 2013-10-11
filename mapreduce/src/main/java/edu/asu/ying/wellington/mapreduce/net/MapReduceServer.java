package edu.asu.ying.wellington.mapreduce.net;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.wellington.MapReduceService;
import edu.asu.ying.wellington.mapreduce.job.JobService;

/**
 * {@code MapReduceServer} is the layer between the network and the {@link MapReduceService}.
 */
public final class MapReduceServer implements MapReduceService {

  private final LocalPeer localPeer;

  public MapReduceServer(final LocalPeer localPeer) {
    this.localPeer = localPeer;
  }

  @Override
  public JobService getJobService() {
    return null;
  }
}
