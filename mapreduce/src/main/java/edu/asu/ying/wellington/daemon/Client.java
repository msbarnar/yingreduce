package edu.asu.ying.wellington.daemon;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.BasicConfigurator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Scanner;

import edu.asu.ying.p2p.kad.KadP2PModule;
import edu.asu.ying.test.ExampleMapReduceJob;
import edu.asu.ying.wellington.WellingtonModule;
import edu.asu.ying.wellington.dfs.Path;
import edu.asu.ying.wellington.dfs.client.DFSClient;
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
  public static void main(String[] args) {
    Client app = new Client(args);
    try {
      app.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the appropriate services, but does not start them.
   */
  private Client(String[] args) {
    BasicConfigurator.configure();
  }

  private void connectNodes(Daemon[] instances) {
    File nodeList = new File(System.getProperty("user.home").concat("/nodes"));
    BufferedReader reader;

    String line;
    try {
      reader = new BufferedReader(new FileReader(nodeList));
      while (reader.ready()) {
        line = reader.readLine();
        if (line == null) {
          break;
        }

        for (int i = 0; i < instances.length; i++) {
          System.out.println("Instance " + i + " connect to " + line);
          instances[i].join(URI.create(String.format("//%s:5000", line)));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File readInputFile() {
    return new File(System.getProperty("user.home") + "/mapreduce/data/lipsum10m.txt");
  }

  Scanner scanner = new Scanner(System.in);

  private String readCommand() {
    System.out.println("=========================");
    System.out.println("q: quit");
    System.out.println("c: connect to physical peers");
    System.out.println("u: upload data");
    System.out.println("j: schedule a job");

    return scanner.nextLine().trim().toLowerCase();
  }

  /**
   * Starts the initialized services, transitioning the daemon to the {@code Running} state.
   */
  private void start() throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(System.getProperty("user.home") + "/pagesize")));
    String pageSize = reader.readLine().trim();
    reader.close();

    reader = new BufferedReader(new FileReader(new File(System.getProperty("user.home") + "/numnodes")));
    int numNodes = Integer.parseInt(reader.readLine().trim());

    // Spawn virtual nodes
    Daemon[] instances = new Daemon[numNodes];
    Injector injector = null;
    for (int i = 0; i < instances.length; i++) {
      injector = Guice.createInjector(
          new KadP2PModule().setProperty("p2p.port", Integer.toString(5000 + i)))
          .createChildInjector(
              new WellingtonModule()
                  .setProperty("dfs.store.path", System.getProperty("user.home") + "/dfs")
                  .setProperty("dfs.page.capacity", pageSize));

      instances[i] = injector.getInstance(Daemon.class);
      System.out.println(instances[i].getPeer().getName());
      if (i > 0) {
        instances[i].join(instances[i - 1]);
      }
    }

    System.out.println(String.format("%d daemons running", instances.length));

    String command;
    while (!(command = readCommand()).equals("q")) {
      switch (command) {
        case "c":
          connectNodes(instances);
          break;
        case "u":
          // Read the input file
          File inputFile = readInputFile();

          if (injector == null) {
            throw new RuntimeException("No injector");
          }
          DFSClient dfsClient = injector.getInstance(DFSClient.class);

          // Create a new file in the DFS and write the contents of the input file
          Path path = new Path("lipsum");
          try (OutputStream ostream = dfsClient
              .getOutputStream(new edu.asu.ying.wellington.dfs.File(path),
                               edu.asu.ying.wellington.dfs.File.OutputMode.CreateNew)) {

            try (InputStream istream = new BufferedInputStream(new FileInputStream(inputFile))) {
              ByteStreams.copy(istream, ostream);
            }
          }

          System.out.println("Waiting for transfers to finish");
          try {
            dfsClient.waitPendingTransfers();
          } catch (InterruptedException ignored) {
          }
          System.out.println("All transfers finished");
          break;
        case "j":
          reader = new BufferedReader(new FileReader(new File(System.getProperty("user.home") + "/numjobs")));
          int numJobs = Integer.parseInt(reader.readLine().trim());
          int numConcurrent = Integer.parseInt(reader.readLine().trim());
          reader.close();

          System.out.println("Starting " + (numJobs / numConcurrent) + " groups of " + numConcurrent + " jobs");
          JobClient client = injector.getInstance(JobClient.class);
          for (int j = 0; j < (numJobs / numConcurrent); j++) {
            for (int i = 0; i < numConcurrent; i++) {
              JobConf job = ExampleMapReduceJob.createJob();
              try {
                client.runJob(job);
              } catch (JobException e) {
                throw new RuntimeException(e);
              }
            }
            System.out.println("Press enter to run next group of jobs");
            scanner.nextLine();
          }
          break;
      }
    }

    for (Daemon instance : instances) {
      instance.stop();
    }
    try {
      Thread.sleep(3000);
    } catch (InterruptedException ignored) {
    }
    System.exit(0);

    // Read the file from the DFS
    /*File outputFile = new File(System.getProperty("user.home") + "/dfs/downloaded-lipsum.txt");
    try (OutputStream ostream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
      try (InputStream istream
               = dfsClient.getInputStream(new edu.asu.ying.wellington.dfs.File(path))) {
        ByteStreams.copy(istream, ostream);
      }
    }*/

    System.out.println("Scheduling job");

    /**************************** Job scheduling *******************************/
  }
}
