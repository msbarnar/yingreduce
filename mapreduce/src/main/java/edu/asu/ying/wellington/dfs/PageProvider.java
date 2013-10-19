package edu.asu.ying.wellington.dfs;

/**
 * {@code PageProvider} is the interface for page retrieval and storage on the DFS server.
 * <p/>
 * The provider should provide access to existing pages wherever they reside; in the case of remote
 * pages, this means downloading the page to local cache and referencing it there.
 */
public interface PageProvider {

  ElementReader getReader(PageIdentifier identifier);

  PageWriter getWriter();
}
