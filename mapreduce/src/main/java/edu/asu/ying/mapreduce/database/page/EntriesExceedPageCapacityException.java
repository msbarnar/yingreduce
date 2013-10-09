package edu.asu.ying.mapreduce.database.page;

import java.util.Map;

import edu.asu.ying.mapreduce.database.EntriesException;
import edu.asu.ying.mapreduce.database.Key;
import edu.asu.ying.mapreduce.database.Value;

/**
 *
 */
public final class EntriesExceedPageCapacityException extends EntriesException {

  public EntriesExceedPageCapacityException(final Key key, final Value value) {
    super(key, value);
  }

  public EntriesExceedPageCapacityException(final Map.Entry<Key, Value> entry) {
    super(entry);
  }

  public EntriesExceedPageCapacityException(final Map<Key, Value> entries) {
    super(entries);
  }
}
