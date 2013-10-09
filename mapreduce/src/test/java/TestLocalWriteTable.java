import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Random;

import edu.asu.ying.common.sink.Sink;
import edu.asu.ying.database.page.Page;
import edu.asu.ying.database.table.LocalWriteTableImpl;
import edu.asu.ying.database.table.TableID;

/**
 *
 */
public class TestLocalWriteTable {

  private final class MockPageSink implements Sink<Page> {

    private int sizeGot;

    @Override
    public final void offer(final Page page) throws IOException {
      this.sizeGot += page.getSizeBytes();
    }

    public final boolean pass(final int sizeAdded) {
      return this.sizeGot == sizeAdded;
    }
  }

  @Test
  public void ItPagesOut() {

    final MockPageSink mockSink = new MockPageSink();
    final Sink<Iterable<Map.Entry<Key, Value>>> table =
        new LocalWriteTableImpl(TableID.createRandom(), mockSink);

    int sizeAdded = 0;

    final Random rnd = new Random();

    final Deque<Map.Entry<Key, Value>> entries = new ArrayDeque<>();
    for (int k = 0; k < 5 + rnd.nextInt(20); k++) {
      for (int i = 0; i < 5 + rnd.nextInt(20); i++) {
        final byte[] data = new byte[1 + rnd.nextInt(199)];
        sizeAdded += data.length;

        entries
            .add(new AbstractMap.SimpleImmutableEntry<Key, Value>(new StringKey(String.valueOf(i)),
                                                                  new ImmutableValue(data)));
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
