package edu.asu.ying.wellington.dfs.io;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public interface PageOutputStreamProvider {

  OutputStream getPageOutputStream(PageIdentifier id) throws IOException;
}
