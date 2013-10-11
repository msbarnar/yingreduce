package edu.asu.ying.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.database.Entry;
import edu.asu.ying.wellington.database.page.Page;
import edu.asu.ying.wellington.database.table.PageBuilder;
import edu.asu.ying.wellington.io.WritableBytes;
import edu.asu.ying.wellington.io.WritableString;

/**
 *
 */
public class TestLocalWriteTable {

  private final class MockPageSink implements Sink<Page> {

    private int sizeGot;

    @Override
    public boolean offer(final Page page) throws IOException {
      this.sizeGot += page.getSizeBytes();
      return true;
    }

    @Override
    public int offer(final Iterable<Page> objects) throws IOException {
      return 0;
    }

    public final boolean pass(final int sizeAdded) {
      return this.sizeGot == sizeAdded;
    }
  }

  @Test
  public void ItPagesOut() {

    final MockPageSink mockSink = new MockPageSink();
    final Sink<Entry> table =
        new PageBuilder(TableID.createRandom(), mockSink);

    int sizeAdded = 0;

    final Random rnd = new Random();

    final Deque<Entry> entries = new ArrayDeque<>();
    for (int k = 0; k < 5 + rnd.nextInt(20); k++) {
      for (int i = 0; i < 5 + rnd.nextInt(20); i++) {
        final byte[] data = new byte[1 + rnd.nextInt(199)];
        sizeAdded += data.length;

        entries
            .add(new Entry(new WritableString(String.valueOf(i)), new WritableBytes(data)));
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
