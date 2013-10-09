package edu.asu.ying.common.filter;

import com.google.common.base.Preconditions;

/**
 * Matches objects on their class.
 */
public class FilterClass implements Filter {

  private enum MatchType {
    InstanceOf,
    Is,
    IsNull
  }

  public static FilterClass instanceOf(final Class<?> clazz) {
    return new FilterClass(clazz, MatchType.InstanceOf);
  }

  public static FilterClass is(final Class<?> clazz) {
    return new FilterClass(clazz, MatchType.Is);
  }

  public static FilterClass isNull() {
    return new FilterClass(MatchType.IsNull);
  }

  private final Class<?> clazz;
  private final MatchType type;

  private FilterClass(final MatchType type) {
    this.clazz = null;
    this.type = type;
  }

  private FilterClass(final Class<?> clazz, final MatchType type) {
    Preconditions.checkNotNull(clazz);

    this.clazz = clazz;
    this.type = type;
  }

  @Override
  public <V> boolean match(V value) {
    if (this.type == MatchType.IsNull) {
      return (value == null);
    } else {
      if (value == null) {
        return false;
      }
    }
    switch (this.type) {
      case InstanceOf:
        return value.getClass().isInstance(this.clazz);

      case Is:
        return this.clazz.equals(value.getClass());

      default:
        return false;
    }
  }
}
