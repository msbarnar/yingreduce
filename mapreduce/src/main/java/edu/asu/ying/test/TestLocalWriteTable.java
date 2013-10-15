package edu.asu.ying.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.client.PageBuilder;
import edu.asu.ying.wellington.dfs.page.Page;
import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.io.WritableBytes;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.io.WritableString;

/**
 *
 */
public class TestLocalWriteTable {

  private final class MockPageSink implements Sink<Page> {

    private int sizeGot;

    @Override
    public boolean offer(Page page) throws IOException {
      sizeGot += page.getSizeBytes();
      return true;
    }

    @Override
    public int offer(Iterable<Page> objects) throws IOException {
      return 0;
    }

    public boolean pass(int sizeAdded) {
      System.out.println(sizeGot);
      System.out.println(sizeAdded);
      return sizeGot == sizeAdded;
    }
  }

  @Test
  public void ItPagesOut() {

    MockPageSink mockSink = new MockPageSink();
    Sink<Element<WritableString, WritableInt, WritableBytes>> table =
        new PageBuilder<>(TableIdentifier.random(), mockSink);

    int sizeAdded = 0;

    Random rnd = new Random();

    Deque<Element<WritableString, WritableInt, WritableBytes>> entries = new ArrayDeque<>();
    for (int k = 0; k < 5 + rnd.nextInt(20); k++) {
      for (int i = 0; i < 5 + rnd.nextInt(20); i++) {
        byte[] data = new byte[1 + rnd.nextInt(198)];
        sizeAdded += data.length;

        entries.add(new Element<>(new WritableString("stuff"), new WritableInt(i),
                                  new WritableBytes(data)));
      }

      try {
        table.offer(entries);
      } catch (final IOException e) {
        e.printStackTrace();
      }

      entries.clear();
    }

    Assert.assertTrue(mockSink.pass(sizeAdded));
  }
}
