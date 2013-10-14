package edu.asu.ying.wellington.dfs.table;

/**
 *
 */
public class TableNotFoundException extends Exception {

  public TableNotFoundException(TableIdentifier id) {
    super(id.toString());
  }
}
