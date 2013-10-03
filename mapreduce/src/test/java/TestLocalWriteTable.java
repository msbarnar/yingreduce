import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import edu.asu.ying.mapreduce.common.Sink;
import edu.asu.ying.mapreduce.database.element.Element;
import edu.asu.ying.mapreduce.database.element.ImmutableElement;
import edu.asu.ying.mapreduce.database.element.ImmutableKey;
import edu.asu.ying.mapreduce.database.element.ImmutableValue;
import edu.asu.ying.mapreduce.database.page.Page;
import edu.asu.ying.mapreduce.database.table.LocalWriteTable;
import edu.asu.ying.mapreduce.database.table.LocalWriteTableImpl;
import edu.asu.ying.mapreduce.database.table.TableID;

/**
 *
 */
public class TestLocalWriteTable {

  private final class MockPageSink implements Sink<Page> {

    private int sizeGot;

    @Override
    public final void accept(final Page page) throws IOException {
      this.sizeGot += page.getSize();
    }

    public final boolean pass(final int sizeAdded) {
      return this.sizeGot == sizeAdded;
    }
  }

  @Test
  public void ItPagesOut() {

    final MockPageSink mockSink = new MockPageSink();
    final LocalWriteTable table = new LocalWriteTableImpl(TableID.createRandom(), mockSink);

    int sizeAdded = 0;

    final Random rnd = new Random();

    final Deque<Element> elements = new ArrayDeque<>();
    for (int k = 0; k < 5 + rnd.nextInt(20); k++) {
      for (int i = 0; i < 5 + rnd.nextInt(20); i++) {
        final byte[] data = new byte[1 + rnd.nextInt(199)];
        sizeAdded += data.length;

        elements.add(new ImmutableElement(new ImmutableKey(String.valueOf(i), String.valueOf(i)),
                                          new ImmutableValue(data)));
      }

      try {
        table.accept(elements);
      } catch (final IOException e) {
        e.printStackTrace();
      }

      elements.clear();
    }

    Assert.assertTrue(mockSink.pass(sizeAdded));
  }
}
