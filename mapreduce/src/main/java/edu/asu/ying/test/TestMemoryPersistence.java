package edu.asu.ying.test;

import com.google.common.base.Charsets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.asu.ying.p2p.kad.KadP2PModule;
import edu.asu.ying.wellington.WellingtonModule;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.SerializingBoundedPage;
import edu.asu.ying.wellington.dfs.SerializingPage;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.dfs.io.PageInputStream;
import edu.asu.ying.wellington.dfs.io.PageOutputStream;
import edu.asu.ying.wellington.dfs.persistence.MemoryPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.io.WritableString;

/**
 *
 */
public class TestMemoryPersistence {

  @Test
  public void itReadsAndWritesBytes() throws IOException {
    final String message = "My name is matt!";
    final String pageName = "mytable~0";

    Injector injector
        = Guice.createInjector(new KadP2PModule())
        .createChildInjector(new WellingtonModule().setProperty("dfs.store.path",
                                                                "/Users/matthew/Desktop/dfs"));
    Persistence persist = injector.getInstance(Key.get(Persistence.class,
                                                       MemoryPersistence.class));
    // Write
    PageOutputStream
        output = persist.getOutputStream(PageIdentifier.forString(pageName));
    byte[] messageBytes = message.getBytes(Charsets.UTF_8);
    output.write(messageBytes);
    output.close();

    // Read
    InputStream
        input = persist.getInputStream(PageIdentifier.forString(pageName));
    byte[] buffer = new byte[messageBytes.length];
    int count = input.read(buffer);
    Assert.assertEquals(count, messageBytes.length);
    String deserialized = new String(buffer, Charsets.UTF_8);
    Assert.assertEquals(deserialized, message);
  }

  @Test
  public void itReadsAndWritesPages() throws IOException {
    String tableName = "mytable!";
    List<Element<WritableString, WritableInt>> elements = new ArrayList<>();
    elements.add(new Element<WritableString, WritableInt>(new WritableString("hi!"),
                                                          new WritableInt(1)));
    elements.add(new Element<WritableString, WritableInt>(new WritableString("bye!"),
                                                          new WritableInt(9)));

    Injector injector
        = Guice.createInjector(new KadP2PModule())
        .createChildInjector(new WellingtonModule().setProperty("dfs.store.path",
                                                                "/Users/matthew/Desktop/dfs"));

    Persistence persist = injector.getInstance(Key.get(Persistence.class,
                                                       MemoryPersistence.class));

    SerializingPage<WritableString, WritableInt> page
        = new SerializingBoundedPage<>(TableIdentifier.forString(tableName),
                                       0, 200, WritableString.class, WritableInt.class);
    Assert.assertEquals(page.offer(elements), elements.size());

    // Write
    PageOutputStream output = persist.getOutputStream(page.getID());
    output.write(page);
    output.close();

    // Read
    PageInputStream input = persist.getInputStream(page.getID());
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
