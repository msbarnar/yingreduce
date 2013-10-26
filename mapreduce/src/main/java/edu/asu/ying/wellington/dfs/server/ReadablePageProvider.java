package edu.asu.ying.wellington.dfs.server;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.ReadablePage;
import edu.asu.ying.wellington.dfs.persistence.PageNotAvailableLocallyException;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@code ReadablePageProvider] is the unified interface for accessing pages in local storage,
 * cached in
 * memory, or on remote nodes.
 * <p/>
 * The page provider offers local retrieval options which throw exceptions if the page is not
 * locally available; in that event the provider offers automatic remote retrieval from nodes
 * that should have the page.
 */
public interface ReadablePageProvider {

  /**
   * Returns {@code true} if call to {@link getLocalPage(PageIdentifier)} would not throw a
   * {@link edu.asu.ying.wellington.dfs.persistence.PageNotAvailableLocallyException}.
   */
  boolean isPageLocallyAvailable(PageIdentifier id);

  /**
   * Finds the page associated with {@code id} in local memory or on disk and returns it as a
   * {@link ReadablePage}.
   */
  <K extends WritableComparable, V extends Writable> ReadablePage<K, V>
  getLocal(PageIdentifier id) throws PageNotAvailableLocallyException;
}
