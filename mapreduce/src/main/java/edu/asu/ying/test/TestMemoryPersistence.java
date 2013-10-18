package edu.asu.ying.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import org.junit.Test;

import java.io.IOException;

import edu.asu.ying.p2p.kad.KadP2PModule;
import edu.asu.ying.wellington.WellingtonModule;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.io.PageOutputStream;
import edu.asu.ying.wellington.dfs.persistence.MemoryPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;

/**
 *
 */
public class TestMemoryPersistence {

  @Test
  public void itStores() {
    Injector injector
        = Guice.createInjector(new KadP2PModule())
        .createChildInjector(new WellingtonModule().setProperty("dfs.store.path",
                                                                "/Users/matthew/Desktop/dfs"));
    Persistence persist = injector.getInstance(Key.get(Persistence.class, MemoryPersistence.class));
    try {
      PageOutputStream
          stream = persist.getProvider().getOutputStream(PageIdentifier.forString("mytable~0"));
      stream.write("My name is matt!".getBytes());
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
