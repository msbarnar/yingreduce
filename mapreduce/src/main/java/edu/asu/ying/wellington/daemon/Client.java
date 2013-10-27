package edu.asu.ying.wellington.daemon;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.LogManager;

import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.kad.KadP2PModule;
import edu.asu.ying.wellington.WellingtonModule;
import edu.asu.ying.wellington.dfs.InvalidPathException;
import edu.asu.ying.wellington.dfs.Path;
import edu.asu.ying.wellington.dfs.client.DFSClient;

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
    final Daemon[] instances = new Daemon[5];

    Injector injector = null;
    for (int i = 0; i < instances.length; i++) {
      injector = Guice.createInjector(
          new KadP2PModule().setProperty("p2p.port", Integer.toString(5000 + i)))
          .createChildInjector(
              new WellingtonModule()
                  .setProperty("dfs.store.path", System.getProperty("user.home") + "/dfs")
                  .setProperty("dfs.page.capacity", "10"));

      instances[i] = injector.getInstance(Daemon.class);
      System.out.println(instances[i].getPeer().getName());
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

    for (RemotePeer peer : instances[0].getPeer().findPeers("hello", 10)) {
      try {
        System.out.println(peer.getName());
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    DFSClient dfsClient = injector.getInstance(DFSClient.class);
    Path path;
    try {
      path = new Path("tests/myfile");
    } catch (InvalidPathException e) {
      throw new RuntimeException(e);
    }
    Random rnd = new Random();
    try (OutputStream ostream = dfsClient
        .getOutputStream(new edu.asu.ying.wellington.dfs.File(path),
                         edu.asu.ying.wellington.dfs.File.OutputMode.CreateNew)) {

      byte[] buffer = new byte[10];
      for (int i = 0; i < 10; i++) {
        for (int j = 0; j < buffer.length; j++) {
          buffer[j] = (byte) i;
        }
        ostream.write(buffer);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();

    for (Daemon instance : instances) {
      instance.getPeer().close();
    }
    /*JobClient client = injector.getInstance(JobClient.class);
    JobConf job = ExampleMapReduceJob.createJob();
    try {
      client.runJob(job);
    } catch (JobException e) {
      throw new RuntimeException(e);
    }*/

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
                            args.index()));
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
