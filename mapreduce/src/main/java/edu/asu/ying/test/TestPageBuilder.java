package edu.asu.ying.test;

import org.junit.Assert;
import org.junit.Test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.PageMetadata;
import edu.asu.ying.wellington.dfs.client.PageBuilder;
import edu.asu.ying.wellington.io.WritableBytes;
import edu.asu.ying.wellington.io.WritableInt;

/**
 *
 */
public class TestPageBuilder {

  private final class MockPageSink implements Sink<PageMetadata> {

    private boolean got;

    @Override
    public boolean offer(PageMetadata pageMetadata) throws IOException {
      got = true;
      return true;
    }

    @Override
    public int offer(Iterable<PageMetadata> objects) throws IOException {
      throw new NotImplementedException();
    }

    public boolean pass() {
      return got;
    }
  }

  @Test
  public void ItPagesOut() throws IOException {

    MockPageSink mockSink = new MockPageSink();
    Class<WritableInt> keyClass = WritableInt.class;
    Class<WritableBytes> valueClass = WritableBytes.class;
    PageBuilder<WritableInt, WritableBytes> pageBuilder =
        new PageBuilder<>("hello", mockSink, keyClass, valueClass);

    for (int i = 0; i < PageBuilder.DEFAULT_PAGE_CAPACITY_BYTES; i++) {
      pageBuilder.offer(new Element<>(new WritableInt(0xBFFE),
                                      new WritableBytes(new byte[1])));
    }

    Assert.assertTrue(mockSink.pass());
  }
}
