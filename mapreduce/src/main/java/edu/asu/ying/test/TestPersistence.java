package edu.asu.ying.test;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import edu.asu.ying.p2p.kad.KadP2PModule;
import edu.asu.ying.wellington.WellingtonModule;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.SerializingBoundedPage;
import edu.asu.ying.wellington.dfs.SerializingPage;
import edu.asu.ying.wellington.dfs.io.PageInputStream;
import edu.asu.ying.wellington.dfs.io.PageWriter;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.MemoryPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.io.WritableString;

/**
 *
 */
public class TestPersistence {

  String tableName = "mytable!";
  List<Element<WritableString, WritableInt>> elements = new ArrayList<>();

  public TestPersistence() {
    elements.add(new Element<>(new WritableString(UUID.randomUUID().toString()),
                               new WritableInt((new Random()).nextInt())));
    elements.add(new Element<>(new WritableString(UUID.randomUUID().toString()),
                               new WritableInt((new Random()).nextInt())));
  }

  @Test
  public void inMemory() throws IOException {
    Injector injector
        = Guice.createInjector(new KadP2PModule())
        .createChildInjector(new WellingtonModule().setProperty("dfs.store.path",
                                                                "/Users/matthew/Desktop/dfs"));

    Persistence persist = injector.getInstance(Key.get(Persistence.class,
                                                       MemoryPersistence.class));

    SerializingPage<WritableString, WritableInt> page
        = new SerializingBoundedPage<>(tableName, 0, 200, WritableString.class, WritableInt.class);
    Assert.assertEquals(page.offer(elements), elements.size());

    // Write
    persist.getWriter().write(page);

    // Read
    PageInputStream input = persist.getInputStream(page.getId());
    Page<WritableString, WritableInt> deserialized = input.readPage();
    Iterator<Element<WritableString, WritableInt>> iter = deserialized.iterator();
    Iterator<Element<WritableString, WritableInt>> iter2 = elements.iterator();
    int i = 0;
    while (iter.hasNext() && iter2.hasNext()) {
      i++;
      Element<WritableString, WritableInt> a = iter.next();
      Element<WritableString, WritableInt> b = iter2.next();
      Assert.assertTrue(a.getKey().equals(b.getKey()));
      Assert.assertTrue(a.getValue().equals(b.getValue()));
    }
    Assert.assertEquals(i, elements.size());
  }

  @Test
  public void onDisk() throws IOException {
    String tmpPath = Files.createTempDir().toString();

    Injector injector
        = Guice.createInjector(new KadP2PModule())
        .createChildInjector(new WellingtonModule().setProperty("dfs.store.path",
                                                                tmpPath));

    Persistence persist = injector.getInstance(Key.get(Persistence.class,
                                                       DiskPersistence.class));

    SerializingPage<WritableString, WritableInt> page
        = new SerializingBoundedPage<>(tableName, 0, 200, WritableString.class, WritableInt.class);
    Assert.assertEquals(page.offer(elements), elements.size());

    // Write
    PageWriter output = persist.getWriter();
    output.write(page);

    // Read
    PageInputStream input = persist.getInputStream(page.getId());
    Page<WritableString, WritableInt> deserialized = input.readPage();
    Iterator<Element<WritableString, WritableInt>> iter = deserialized.iterator();
    Iterator<Element<WritableString, WritableInt>> iter2 = elements.iterator();
    int i = 0;
    while (iter.hasNext() && iter2.hasNext()) {
      i++;
      Element<WritableString, WritableInt> a = iter.next();
      Element<WritableString, WritableInt> b = iter2.next();
      Assert.assertTrue(a.getKey().equals(b.getKey()));
      Assert.assertTrue(a.getValue().equals(b.getValue()));
    }
    Assert.assertEquals(i, elements.size());
  }
}
