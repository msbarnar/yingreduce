package edu.asu.ying.test;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.dfs.File;
import edu.asu.ying.wellington.dfs.io.BufferedPageFetchStream;

/**
 *
 */
public class TestBufferedPageFetchStream {

  @Test
  public void itCachesAndReads() throws IOException {
    File file = new File("my/cool/file");
    try (InputStream istream = new BufferedPageFetchStream(file, 10 * 1024 * 1024, null)) {
      int b = 0;
      while (-1 != (b = istream.read())) {
        System.out.write(b);
      }
    }
  }
}
