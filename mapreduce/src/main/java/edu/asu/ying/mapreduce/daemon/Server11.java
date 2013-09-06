package edu.asu.ying.mapreduce.daemon;

import java.io.IOException;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.job.MapReduceJob;
import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.yingtable.TableID;
import edu.asu.ying.p2p.node.kad.KadNodeURL;

/**
 * The main entry point for the node daemon. {@code Server9} starts the table, scheduling, and
 * interface services before attaching the local node to an existing Kademlia network.
 */
public class Server11 {

  /**
   * Daemon entry point.
   */
  public static void main(final String[] args) {
    // TODO: Logging
    System.out.println(
        "YingReduce 0.2.1\nCopyright \u00A9 2013 Ying Lab, Arizona State University");
    System.out.println("For help contact Matthew Barnard: msbarnar@gmail.com");
    System.out.println();

    final Server11 app = new Server11(args);
    app.start();
  }

  /**
   * Initializes the appropriate services, but does not start them.
   */
  private Server11(final String[] args) {
    // TODO: Logging
    System.out.println("SERVER 11");
    System.out.println("Getting things ready...");
  }

  /**
   * Starts the initialized services, transitioning the daemon to the {@code Running} state.
   */
  private void start() {
    // TODO: Logging
    System.out.println("Starting the application...");

    final Daemon instance2 = new Daemon(5002);
    try {
      instance2.getLocalNode().join(new KadNodeURL("//149.169.30.10:5000"));
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    }

    System.out.println("... and we're rolling!");
    System.out.println();
    System.out.println("Visit http://localhost:8887/ to administer the local node.\n\n");

    LocalScheduler sched = null;
    sched = instance2.getLocalNode().getScheduler();

    if (sched != null) {
      for (int i = 0; i < 3; i++) {
        final Job job = new MapReduceJob(new TableID("mytable"));
        job.setStartTime();
        final JobSchedulingResult result = sched.createJob(job);
      }
    }
  }
}
