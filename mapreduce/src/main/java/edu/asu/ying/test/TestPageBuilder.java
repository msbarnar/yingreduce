package edu.asu.ying.test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.PageDistributor;
import edu.asu.ying.wellington.dfs.SerializedReadablePage;
import edu.asu.ying.wellington.dfs.client.PageBuilder;
import edu.asu.ying.wellington.io.WritableBytes;
import edu.asu.ying.wellington.io.WritableString;
import edu.asu.ying.wellington.ybase.Element;

/**
 *
 */
public class TestPageBuilder {

  private static final MockPageDistributor distributor = new MockPageDistributor();

  private static class MockModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(new TypeLiteral<Sink<SerializedReadablePage>>() {
      })
          .annotatedWith(PageDistributor.class)
          .toInstance(distributor);
      bind(Integer.class)
          .annotatedWith(Names.named("dfs.page.capacity"))
          .toInstance(30);
    }
  }

  private static class MockPageDistributor implements Sink<SerializedReadablePage> {

    private boolean pass = false;

    @Override
    public void accept(SerializedReadablePage object) throws IOException {
      pass = true;
    }

    public boolean didPass() {
      return pass;
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void itPagesOut() throws Exception {

    Injector injector = Guice.createInjector(new MockModule());
    PageBuilder<WritableString, WritableBytes> pb = injector.getInstance(PageBuilder.class);
    pb.open("mytable", WritableString.class, WritableBytes.class);
    pb.accept(new Element<>(new WritableString("Hi!"), new WritableBytes(new byte[15])));
    pb.accept(new Element<>(new WritableString("Hi!"), new WritableBytes(new byte[15])));

    Assert.assertTrue(distributor.didPass());
  }
}
