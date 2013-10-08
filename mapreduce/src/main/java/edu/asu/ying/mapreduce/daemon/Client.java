package edu.asu.ying.mapreduce.daemon;

import edu.asu.ying.mapreduce.database.table.TableID;
import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.job.MapReduceJob;
import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;

/**
 * The main entry point for the node daemon. {@code Server} starts the table, scheduling, and
 * interface services before attaching the local node to an existing Kademlia network.
 */
public class Client {

  /**
   * Daemon entry point.
   */
  public static void main(final String[] args) {
    // TODO: Logging
    // System.out.println(
    //    "YingReduce 0.2.1 Copyright \u00A9 2013 Ying Lab, Arizona State University");

    final Client app = new Client(args);
    app.start();
  }

  /**
   * Initializes the appropriate services, but does not start them.
   */
  private Client(final String[] args) {
  }

  /**
   * Starts the initialized services, transitioning the daemon to the {@code Running} state.
   */
  private void start() {
    final Daemon[] instances = new Daemon[2];

    for (int i = 0; i < instances.length; i++) {
      instances[i] = new Daemon(5000 + i);
      if (i > 0) {
        instances[i].join(instances[i - 1]);
      }
    }

    System.out.println(String.format("%d daemons running", instances.length));

    final File nodeList = new File(System.getProperty("user.home").concat("/nodes"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(nodeList));
    } catch (final IOException e) {
      e.printStackTrace();
    }

    if (reader != null) {
      String line = null;
      try {
        while (reader.ready()) {
          line = reader.readLine();
          if (line == null) {
            break;
          }

          instances[0].join(URI.create(String.format("//%s:5000", line)));
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    LocalScheduler sched = null;
    sched = instances[0].getLocalNode().getScheduler();

    if (sched != null) {
      for (int i = 0; i < 1; i++) {
        final Job job = new MapReduceJob(new TableID("hoblahsh"));
        final JobSchedulingResult result = sched.createJob(job);
      }
    }
  }
}
