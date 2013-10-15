package edu.asu.ying.test;

import org.junit.Assert;
import org.junit.Test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.dfs.client.PageBuilder;
import edu.asu.ying.wellington.dfs.io.ElementOutputStream;
import edu.asu.ying.wellington.io.WritableBytes;
import edu.asu.ying.wellington.io.WritableInt;

/**
 *
 */
public class TestLocalWriteTable {

  private final class MockPageSink implements Sink<Page> {

    private int sizeGot;
    public double effic = 0.0;
    public String sortOrder = "None";

    @Override
    public boolean offer(Page page) throws IOException {
      double eff = (double) page.getSizeBytes() / page.getCapacityBytes();
      effic += eff;
      effic /= 2.0;
      sizeGot += page.getSizeBytes();
      return true;
    }

    @Override
    public int offer(Iterable<Page> objects) throws IOException {
      throw new NotImplementedException();
    }

    public boolean pass(int sizeAdded) {
      return sizeGot == sizeAdded;
    }
  }

  @Test
  public void ItPagesOut() {

    MockPageSink mockSink = new MockPageSink();
    ElementOutputStream elementWriter = new PageBuilder(TableIdentifier.random(), mockSink);

    int sizeAdded = 0;

    Random rnd = new Random();

    Deque<Element> elements = new ArrayDeque<>();
    for (int k = 0; k < 50; k++) {
      for (int i = 0; i < 60; i++) {
        byte[] data = new byte[80 + rnd.nextInt(100)];
        sizeAdded += WritableInt.SIZE + WritableBytes.SIZE + data.length;

        elements.add(new Element(new WritableInt(0xD),
                                 new WritableBytes(data)));
      }

      try {
        int written = elementWriter.write(elements);
        Assert.assertEquals(written, elements.size());
        elementWriter.flush();

      } catch (final IOException e) {
        throw new AssertionError("Failed to write element", e);
      }

      elements.clear();
    }

    System.out
        .println(String.format("Average packing efficiency: %,.2f%%", 100.0 * mockSink.effic));

    Assert.assertTrue(mockSink.pass(sizeAdded));
  }
}
