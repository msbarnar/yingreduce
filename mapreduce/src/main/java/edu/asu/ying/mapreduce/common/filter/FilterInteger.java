package edu.asu.ying.mapreduce.common.filter;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Matches objects on their class.
 */
public class FilterInteger implements Filter {

  private enum MatchType {
    Equals,
    LessThan,
    GreaterThan,
  }
  public static FilterInteger equalTo(final int target) {
    return new FilterInteger(target, MatchType.Equals);
  }
  public static FilterInteger lessThan(final int target) {
    return new FilterInteger(target, MatchType.LessThan);
  }
  public static FilterInteger greaterThan(final int target) {
    return new FilterInteger(target, MatchType.GreaterThan);
  }
  public static FilterInteger isNegative(final int target) {
    return new FilterInteger(0, MatchType.LessThan);
  }
  public static FilterInteger isPositive(final int target) {
    return new FilterInteger(0, MatchType.GreaterThan);
  }
  public static FilterInteger isZero(final int target) {
    return new FilterInteger(0, MatchType.Equals);
  }

  private final Integer target;
  private final MatchType type;

  private FilterInteger(final MatchType type) {
    this.target = 0;
    this.type = type;
  }
  private FilterInteger(final int target, final MatchType type) {
    this.target = target;
    this.type = type;
  }

  @Override
  public <V> boolean match(final V value) {
    try {
      return this.match((Integer) value);
    } catch (final ClassCastException e) {
      return false;
    }
  }

  public boolean match(final Integer value) {
    switch (this.type) {
      case Equals:
        return this.target.equals(value);
      case GreaterThan:
        return (this.target.compareTo(value) < 0);
      case LessThan:
        return (this.target.compareTo(value) > 0);

      default:
        return false;
    }
  }
}
