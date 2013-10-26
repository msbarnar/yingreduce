package edu.asu.ying.test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.kad.KadP2PModule;

/**
 *
 */
public class TestP2P {

  private static final int nodeCount = 5;
  private static List<LocalPeer> peers = new ArrayList<>();

  @BeforeClass
  public static void spawnNodes() throws IOException {
    System.out.println("Starting " + Integer.toString(nodeCount) + " peers");
    Injector injector;
    for (int i = 0; i < nodeCount; i++) {
      injector = Guice.createInjector(
          new KadP2PModule().setProperty("p2p.port", Integer.toString(5000 + i)));
      LocalPeer peer = injector.getInstance(LocalPeer.class);
      if (i > 0) {
        peer.join(URI.create("openkad.udp://127.0.0.1:" + Integer.toString(5000 + (i - 1))));
      }
      peers.add(peer);
    }
  }

  @AfterClass
  public static void killNodes() {
    for (LocalPeer peer : peers) {
      peer.close();
    }
  }

  /**
   * Fails assertion if any of the nodes don't have unique keys.
   */
  @Test
  public void keysAreUnique() throws Exception {
    Set<String> keys = new HashSet<>();
    for (LocalPeer peer : peers) {
      Assert.assertTrue(keys.add(peer.getName()));
    }
  }

  /**
   * Fails assertion if a "find nodes" operation doesn't return the same K nodes from every single
   * node. That would imply that all nodes won't find the same initial node or responsible node.
   */
  @Test
  public void findNodesIsConsistent() throws Exception {

    final int howManyShouldMatch = 3;
    final String searchString = "hello";

    List<List<RemotePeer>> allPeers = new ArrayList<>();
    for (LocalPeer peer : peers) {
      List<RemotePeer> found = peer.findPeers(searchString, howManyShouldMatch);
      System.out.println(found);
      allPeers.add(found);
    }

    List<String> officialKeys = new ArrayList<>();
    for (List<RemotePeer> remotePeers : allPeers) {
      if (officialKeys.isEmpty()) {
        // Populate official keys
        for (RemotePeer remotePeer : remotePeers) {
          officialKeys.add(remotePeer.getName());
        }
      } else {
        // Check against official keys
        for (int i = 0; i < howManyShouldMatch; i++) {
          Assert.assertEquals(remotePeers.get(i).getName(), officialKeys.get(i));
        }
      }
    }
  }
}
