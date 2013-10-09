package edu.asu.ying.mapreduce.database.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.asu.ying.mapreduce.database.DatabaseException;
import edu.asu.ying.mapreduce.io.Writable;

/**
 *
 */
public final class EntriesExceedPageCapacityException extends DatabaseException {

  private final List<Writable> keys = new ArrayList<>();

  public EntriesExceedPageCapacityException(final Writable key) {
    this.keys.add(key);
  }

  public EntriesExceedPageCapacityException(final Collection<Writable> keys) {
    this.keys.addAll(keys);
  }

  public final List<Writable> getKeys() {
    return this.keys;
  }
}
