package edu.asu.ying.common.filter;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Matches objects on their class.
 */
public class FilterString implements Filter {

  private enum MatchType {
    Equals,
    StartsWith,
    EndsWith,
    IsNullOrEmpty
  }

  public static FilterString equalTo(final String target) {
    return new FilterString(target, MatchType.Equals);
  }

  public static FilterString isNullOrEmpty() {
    return new FilterString(MatchType.IsNullOrEmpty);
  }

  private final String target;
  private final MatchType type;

  private FilterString(final MatchType type) {
    this.target = "";
    this.type = type;
  }

  private FilterString(final String target, final MatchType type) {
    Preconditions.checkNotNull(target);

    this.target = target;
    this.type = type;
  }

  @Override
  public <V> boolean match(final V value) {
    if (value == null) {
      return this.match(null);
    } else {
      return this.match(String.valueOf(value));
    }
  }

  public boolean match(final String value) {
    if (this.type == MatchType.IsNullOrEmpty) {
      return Strings.isNullOrEmpty(value);
    } else {
      if (value == null) {
        return false;
      }
    }
    switch (this.type) {
      case Equals:
        return this.target.equals(value);

      default:
        return false;
    }
  }
}
