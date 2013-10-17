package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.dfs.io.PageReader;

/**
 * {@code PageProvider} is the interface for page retrieval and storage on the DFS server.
 * <p/>
 * The provider should provide access to existing pages wherever they reside; in the case of remote
 * pages, this means downloading the page to local cache and referencing it there.
 */
public interface PageProvider {

  PageReader getReader(PageIdentifier identifier);

  void storeLocally(SerializedPage page);
}
