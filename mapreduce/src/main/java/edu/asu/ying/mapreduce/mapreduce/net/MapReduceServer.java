package edu.asu.ying.mapreduce.mapreduce.net;

import edu.asu.ying.mapreduce.MapReduceService;
import edu.asu.ying.mapreduce.mapreduce.job.JobService;
import edu.asu.ying.p2p.LocalPeer;

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
