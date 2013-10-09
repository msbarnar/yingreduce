package edu.asu.ying.mapreduce.database;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Specifies one or more entries that were too large to fit on the given page.
 */
public abstract class EntriesException extends DatabaseException {

  private final Map<Key, Value> entries;

  public EntriesException(final Key key, final Value value) {
    this.entries = ImmutableMap.of(key, value);
  }

  public EntriesException(final Map.Entry<Key, Value> entry) {
    this(entry.getKey(), entry.getValue());
  }

  public EntriesException(final Map<Key, Value> entries) {
    this.entries = ImmutableMap.copyOf(entries);
  }

  public EntriesException(final Iterable<Map.Entry<Key, Value>> entries) {
    final ImmutableMap.Builder<Key, Value> builder = ImmutableMap.builder();
    for (final Map.Entry<Key, Value> entry : entries) {
      builder.put(entry);
    }
    this.entries = builder.build();
  }

  public final Map<Key, Value> getEntries() {
    return this.entries;
  }
}
