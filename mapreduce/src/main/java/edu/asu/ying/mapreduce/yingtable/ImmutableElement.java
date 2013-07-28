package edu.asu.ying.mapreduce.yingtable;

/**
 * {@code ImmutableElement} implements the table element as an unsorted map of the
 * {@code (row, column)} pair to a sorted map of {@code timestamp->byte[]}.
 * </p>
 * While a table therefore appears to function as a three-dimensionally-indexed map, it is more
 * accurately defined as a 2D map of {@code (row, column)} to a tree of byte arrays.
 */
public final class ImmutableElement implements Element {

  private final static long SerialVersionUID = 1L;

  private final Key key;
  private final byte[] value;

  public ImmutableElement(final Key key, final byte[] value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public Key getKey() {
    return this.key;
  }

  @Override
  public byte[] getValue() {
    return this.value;
  }
}
