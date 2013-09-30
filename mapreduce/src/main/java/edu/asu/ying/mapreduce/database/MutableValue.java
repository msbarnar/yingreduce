package edu.asu.ying.mapreduce.database;

import edu.asu.ying.mapreduce.database.element.Element;

/**
 *
 */
public class MutableValue implements Element.Value {

  private final byte[] content;

  public MutableValue(final byte[] content) {
    this.content = content;
  }

  @Override
  public long getSize() {
    return this.content.length;
  }

  @Override
  public byte[] getContent() {
    return this.content;
  }
}
