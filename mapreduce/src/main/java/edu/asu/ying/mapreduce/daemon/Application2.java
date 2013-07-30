package edu.asu.ying.mapreduce.daemon;

import java.io.IOException;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.job.MapReduceJob;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.node.kad.KadNodeURL;
import edu.asu.ying.mapreduce.yingtable.TableID;

/**
 * The main entry point for the node daemon. {@code Application} starts the table, scheduling, and
 * interface services before attaching the local node to an existing Kademlia network.
 */
public class Application2 {

  /**
   * Daemon entry point.
   */
  public static void main(final String[] args) {
    // TODO: Logging
    System.out.println(
        "YingReduce 0.2.1\nCopyright \u00A9 2013 Ying Lab, Arizona State University");
    System.out.println("For help contact Matthew Barnard: msbarnar@gmail.com");
    System.out.println();

    final Application2 app = new Application2(args);
    app.start();
  }

  /**
   * Initializes the appropriate services, but does not start them.
   */
  private Application2(final String[] args) {
    // TODO: Logging
    System.out.println("Getting things ready...");
  }

  /**
   * Starts the initialized services, transitioning the daemon to the {@code Running} state.
   */
  private void start() {
    // TODO: Logging
    System.out.println("Starting the application...");

    final Daemon instance2 = new Daemon(5001);
    try {
      instance2.getLocalNode().join(new KadNodeURL("//127.0.0.1:5000"));
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    }

    System.out.println("... and we're rolling!");
    System.out.println();
    System.out.println("Visit http://localhost:8887/ to administer the local node.\n\n");

    Scheduler sched = null;
    try {
      sched = instance2.getLocalNode().getScheduler();
    } catch (final RemoteException e) {
      e.printStackTrace();
    }

    if (sched != null) {
      try {
        final Job job = new MapReduceJob(new TableID("mytable"));
        final JobSchedulingResult result = sched.createJob(job);
        System.out.println(String.format("Scheduling job %s... %s on node %s",
                                         result.getJob().getId(),
                                         result.getResult().toString(),
                                         result.getNodeUri().toString()));
      } catch (final RemoteException e) {
        e.printStackTrace();
      }
    }
  }
}
