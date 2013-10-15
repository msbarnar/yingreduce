package edu.asu.ying.wellington.dfs;

/**
 *
 */
public class TableNotFoundException extends Exception {

  public TableNotFoundException(TableIdentifier id) {
    super(id.toString());
  }
}
