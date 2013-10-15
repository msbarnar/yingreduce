package edu.asu.ying.wellington.dfs;

/**
 *
 */
public interface Table {

  TableIdentifier getID();

  int getNumPages();

  boolean hasPage(int index);
}
