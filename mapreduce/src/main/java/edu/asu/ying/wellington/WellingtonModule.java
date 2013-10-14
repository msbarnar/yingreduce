package edu.asu.ying.wellington;

import com.google.inject.Binder;
import com.google.inject.Module;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.kad.KadLocalPeer;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.net.DFSServer;
import edu.asu.ying.wellington.mapreduce.job.JobScheduler;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;
import edu.asu.ying.wellington.mapreduce.net.NodeServer;
import edu.asu.ying.wellington.mapreduce.task.TaskScheduler;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 * {@code WellingtonModule} provides the bindings for dependency injection.
 */
public class WellingtonModule implements Module {

  @Override
  public void configure(Binder binder) {
    // P2P Network
    binder.bind(LocalPeer.class).to(KadLocalPeer.class);
    // Service network
    binder.bind(LocalNode.class).to(NodeServer.class);
    // Services
    binder.bind(JobService.class).to(JobScheduler.class);
    binder.bind(TaskService.class).to(TaskScheduler.class);
    binder.bind(DFSService.class).to(DFSServer.class);
  }
}
