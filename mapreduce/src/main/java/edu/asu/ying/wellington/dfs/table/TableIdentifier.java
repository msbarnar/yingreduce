package edu.asu.ying.wellington.dfs.table;

import java.util.UUID;

import edu.asu.ying.wellington.AbstractIdentifier;

/**
 *
 */
public final class TableIdentifier extends AbstractIdentifier {

  public static TableIdentifier random() {
    return new TableIdentifier(UUID.randomUUID().toString());
  }

  public static TableIdentifier forString(String id) {
    return new TableIdentifier(id);
  }

  private static final long SerialVersionUID = 1L;

  private TableIdentifier(String id) {
    super(id);
  }
}
