package edu.asu.ying.mapreduce.database.page;

import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.database.EntriesException;

/**
 *
 */
public final class PageCapacityExceededException extends EntriesException {

  private final Map<Key, Value> entries = new HashMap<>();

  public PageCapacityExceededException(final Map.Entry<Key, Value> entry) {
    super(entry);
  }

  public PageCapacityExceededException(final Map<Key, Value> entries) {
    super(entries);
  }
}
