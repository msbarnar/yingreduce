package edu.asu.ying.wellington.dfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class ElementsExceedPageCapacityException extends IOException {

  private final List<WritableComparable> keys = new ArrayList<>();

  public ElementsExceedPageCapacityException(final WritableComparable key) {
    this.keys.add(key);
  }

  public ElementsExceedPageCapacityException(final Collection<WritableComparable> keys) {
    this.keys.addAll(keys);
  }

  public final List<WritableComparable> getKeys() {
    return this.keys;
  }
}
