package edu.asu.ying.mapreduce.database.table;

/**
 *
 */
public interface Table {

  TableID getId();

  int getPageCount();

  int getMaxPageSize();
}
