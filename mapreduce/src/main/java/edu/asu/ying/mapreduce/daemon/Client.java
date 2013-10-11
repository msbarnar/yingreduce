package edu.asu.ying.mapreduce.daemon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.logging.LogManager;

import edu.asu.ying.database.Entry;
import edu.asu.ying.database.io.WritableBytes;
import edu.asu.ying.database.io.WritableString;
import edu.asu.ying.database.table.PageBuilder;
import edu.asu.ying.database.table.TableID;
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
    LogManager.getLogManager().reset();
  }

  /**
   * Starts the initialized services, transitioning the daemon to the {@code Running} state.
   */
  private void start() {
    final Daemon[] instances = new Daemon[10];

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

    boolean fullyConnected = false;

    if (reader != null) {
      String line = null;
      try {
        while (reader.ready()) {
          line = reader.readLine();
          if (line == null) {
            break;
          }

          instances[0].join(URI.create(String.format("//%s:5000", line)));

          if (line.equals("149.169.30.10")) {
            fullyConnected = true;
          }
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    if (!fullyConnected) {
      LocalScheduler sched = null;
      //sched = instances[0].getLocalPeer().getScheduler();

      if (sched != null) {
        for (int i = 0; i < 1; i++) {
          final Job job = new MapReduceJob(new TableID("hoblahsh"));
          final JobSchedulingResult result = sched.createJob(job);
        }
      }
    }

    for (final Daemon instance : instances) {
      /*instance.getLocalPeer().getPageInSink().onIncomingPage.attach(new EventHandler<Page>() {
        @Override
        public boolean onEvent(Object sender, Page args) {
          System.out.println(
              String.format("[%d] PAGE! %s %d", instance.getPort(), args.getTableId().toString(),
                            args.getIndex()));
          return true;
        }
      });*/
    }

    final PageBuilder pb = null;//new PageBuilder(new TableID("lipsum"),
    //instances[0].getLocalPeer().getPageOutSink());
    try {
      pb.offer(new Entry(new WritableString("hi!"),
                         new WritableBytes("It's a small world after all".getBytes())));
      pb.flush();
      pb.offer(new Entry(new WritableString("hi!"),
                         new WritableBytes("It's a small world after all".getBytes())));
      pb.flush();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
