package edu.asu.ying.test;

import org.junit.Test;

import java.util.Scanner;

import edu.asu.ying.p2p.kad.KadLocalPeer;
import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.net.NodeServer;

/**
 *
 */
public class TestJobDelegator {

  @Test
  public void itDelegatesJobs() throws Exception {
    NodeServer server = new NodeServer(new KadLocalPeer(5000));
    Job job = new Job(TableIdentifier.forString("hi!"));
    job.setResponsibleNode(server.getAsRemote());

    server.getJobService().accept(job);

    System.out.println("Press return to exit");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
  }
}
