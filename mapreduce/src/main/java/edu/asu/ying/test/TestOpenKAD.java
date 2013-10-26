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

import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;
import il.technion.ewolf.kbr.openkad.KadNetModule;

/**
 * Starts up some nodes, runs tests with them, and shuts down them.
 */
public class TestOpenKAD {

  private static class TestNode {

    private final KeybasedRouting kbrNode;
    public final String key;

    public TestNode(int port) throws IOException {
      Injector injector = Guice.createInjector(
          new KadNetModule()
              .setProperty("openkad.keyfactory.keysize", String.valueOf(20))
              .setProperty("openkad.bucket.kbuckets.maxsize", String.valueOf(20))
              .setProperty("openkad.seed", String.valueOf(port))
              .setProperty("openkad.net.udp.port", String.valueOf(port))
              .setProperty("openkad.file.nodes.path",
                           System.getProperty("user.home").concat("/.kadhosts"))
      );

      this.kbrNode = injector.getInstance(KeybasedRouting.class);
      this.kbrNode.create();
      this.key = this.kbrNode.getLocalNode().getKey().toString();
    }

    public void join(final int port) {
      List<URI> bootstrapNodes = new ArrayList<>();
      bootstrapNodes.add(URI.create("openkad.udp://127.0.0.1:" + Integer.toString(port)));
      kbrNode.join(bootstrapNodes);
    }

    public List<String> findNodes(String key) {
      List<String> keys = new ArrayList<>();
      List<Node> nodes = kbrNode.findNode(kbrNode.getKeyFactory().create(key));
      for (Node n : nodes) {
        keys.add(n.getKey().toString());
      }
      return keys;
    }
  }

  private static final int basePort = 5000;
  private static final int nodeCount = 10;
  private static TestNode[] nodes;

  /**
   * Spawns up {@code nodeCount} nodes for running tests.
   */
  @BeforeClass
  public static void spawnNodes() throws Exception {
    System.out.println("Spawning ".concat(Integer.toString(nodeCount)).concat(" nodes"));
    nodes = new TestNode[nodeCount];
    for (int i = 0; i < nodeCount; i++) {
      nodes[i] = new TestNode(basePort + i);
      if (i > 0) {
        nodes[i].join(basePort + (i - 1));
      }
    }
  }

  @AfterClass
  public static void killNodes() {
    System.out.println("Shutting down nodes");
    try {
      for (int i = 0; i < nodeCount; i++) {
        nodes[i].kbrNode.shutdown();
      }
    } catch (Exception ignored) {
    }
    nodes = null;
  }

  /**
   * Fails assertion if any of the nodes don't have unique keys.
   */
  @Test
  public void keysAreUnique() throws Exception {
    Set<String> keys = new HashSet<>();
    for (TestNode node : nodes) {
      Assert.assertTrue(keys.add(node.key));
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

    List<List<String>> allKeys = new ArrayList<>();
    for (TestNode node : nodes) {
      allKeys.add(node.findNodes(searchString));
    }

    List<String> officialKeys = new ArrayList<>();
    for (List<String> keys : allKeys) {
      if (officialKeys.isEmpty()) {
        // Populate official keys
        for (int i = 0; i < howManyShouldMatch; i++) {
          officialKeys.add(keys.get(i));
        }
      } else {
        // Check against official keys
        for (int i = 0; i < howManyShouldMatch; i++) {
          Assert.assertEquals(keys.get(i), officialKeys.get(i));
        }
      }
    }
  }
}
