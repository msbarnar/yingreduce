package edu.asu.ying.wellington.dfs.table;

import edu.asu.ying.wellington.dfs.page.PageIdentifier;

/**
 *
 */
public interface Table {

  TableIdentifier getId();

  boolean hasPage(int index);

  boolean hasPage(PageIdentifier pageID);
}
