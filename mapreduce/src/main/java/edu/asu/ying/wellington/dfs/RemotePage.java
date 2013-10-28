package edu.asu.ying.wellington.dfs;

import java.io.InputStream;
import java.io.Serializable;

/**
 * {@code RemotePage} wraps a page's metadata and a remotely consumable {@link InputStream} of the
 * page's contents.
 */
public interface RemotePage extends Serializable {

  Page metadata();

  InputStream contents();
}
