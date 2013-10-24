package edu.asu.ying.wellington.daemon;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.logging.LogManager;

import edu.asu.ying.p2p.kad.KadP2PModule;
import edu.asu.ying.test.ExampleMapReduceJob;
import edu.asu.ying.wellington.WellingtonModule;
import edu.asu.ying.wellington.mapreduce.job.JobClient;
import edu.asu.ying.wellington.mapreduce.job.JobConf;
import edu.asu.ying.wellington.mapreduce.job.JobException;

/**
 * The com.healthmarketscience.rmiio.main entry point for the node daemon. {@code Server} starts
 * the
 * table, scheduling, and
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
    final Daemon[] instances = new Daemon[3];

    Injector injector = null;
    for (int i = 0; i < instances.length; i++) {
      injector = Guice.createInjector(
          new KadP2PModule().setProperty("p2p.port", Integer.toString(5000 + i)))
          .createChildInjector(new WellingtonModule().setProperty("dfs.store.path",
                                                                  "/Users/Matthew/Desktop/dfs"));

      instances[i] = injector.getInstance(Daemon.class);
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

    JobClient client = injector.getInstance(JobClient.class);
    JobConf job = ExampleMapReduceJob.createJob();
    try {
      client.runJob(job);
    } catch (JobException e) {
      throw new RuntimeException(e);
    }

    if (!fullyConnected) {
      /*LocalScheduler sched = null;
      //sched = instances[0].getLocalPeer();

      if (sched != null) {
        for (int i = 0; i < 1; i++) {
          final Job job = new Job(new TableID("hoblahsh"));
          final JobSchedulingResult result = sched.createJob(job);
        }
      }*/
    }

    for (final Daemon instance : instances) {
      /*instance.getLocalPeer().getPageInSink().onIncomingPage.attach(new EventHandler<HasPageMetadata>() {
        @Override
        public boolean onEvent(Object sender, HasPageMetadata args) {
          System.out.println(
              String.format("[%d] PAGE! %s %d", instance.getPort(), args.getTableId().toString(),
                            args.getIndex()));
          return true;
        }
      });*/
    }

    /*final PageBuilder pb = new PageBuilder(TableIdentifier.forString("lipsum"), instances[0].getLocalPeer().getPageOutSink());
    try {
      pb.offer(new Element(new WritableInt(1), new WritableString("a"),
                         new WritableBytes("It's a small world after all".getBytes())));
      pb.flush();
      pb.offer(new Element(new WritableInt(2), new WritableString("b"),
                         new WritableBytes("It's a small world after all".getBytes())));
      pb.flush();
    } catch (final IOException e) {
      e.printStackTrace();
    }*/
  }
}
