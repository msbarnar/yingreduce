package edu.asu.ying.mapreduce.yingtable;

/**
 *
 */
public class ImmutableValue implements Element.Value {

  private final byte[] content;

  public ImmutableValue(final byte[] content) {
    this.content = content;
  }

  public ImmutableValue(final Element.Value copy) {
    this.content = copy.getContent().clone();
  }

  @Override
  public long getSize() {
    return this.content.length;
  }

  @Override
  public byte[] getContent() {
    return this.content.clone();
  }
}
