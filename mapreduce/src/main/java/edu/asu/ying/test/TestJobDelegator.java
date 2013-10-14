package edu.asu.ying.test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.Test;

import java.util.Scanner;

import edu.asu.ying.wellington.WellingtonModule;
import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;

/**
 *
 */
public class TestJobDelegator {

  @Test
  public void itDelegatesJobs() throws Exception {
    Injector injector = Guice.createInjector(new WellingtonModule()
                                                 .setProperty("p2p.port", "5000"));

    LocalNode node = injector.getInstance(LocalNode.class);
    Job job = new Job(TableIdentifier.forString("hi!"));
    job.setResponsibleNode(node.getAsRemote());

    node.getJobService().accept(job);

    System.out.println("Press return to exit");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
  }
}
