package edu.asu.ying.database.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.asu.ying.database.DatabaseException;
import edu.asu.ying.database.io.WritableComparable;

/**
 *
 */
public final class EntriesExceedPageCapacityException extends DatabaseException {

  private final List<WritableComparable> keys = new ArrayList<>();

  public EntriesExceedPageCapacityException(final WritableComparable key) {
    this.keys.add(key);
  }

  public EntriesExceedPageCapacityException(final Collection<WritableComparable> keys) {
    this.keys.addAll(keys);
  }

  public final List<WritableComparable> getKeys() {
    return this.keys;
  }
}