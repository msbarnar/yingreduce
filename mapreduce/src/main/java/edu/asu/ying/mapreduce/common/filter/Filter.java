package edu.asu.ying.mapreduce.common.filter;

import com.google.common.base.Preconditions;


/**
 * A {@code Filter} is a node in the tree of filters.
 */
// TODO: Abstract this to an interface
public interface Filter {

  /**
   * Maps the value to the node's children and reduces their results.
   */
  <V> boolean match(final V value);

  /**
   * Common filters
   */
  public static final class on {

    public static <T> FilterOnEquals<T> equalTo(final T value) {
      return new FilterOnEquals<T>(value);
    }

    public static Filter allOf(final Filter... filters) {
      Preconditions.checkNotNull(filters);
      return new on.FilterAllOf(filters);
    }

    public static Filter anyOf(final Filter... filters) {
      Preconditions.checkNotNull(filters);
      return new on.FilterAnyOf(filters);
    }

    public static Filter noneOf(final Filter... filters) {
      Preconditions.checkNotNull(filters);
      return new on.FilterNoneOf(filters);
    }

    /**********************************************************************
     * Filters
     */
    /**
     * {@code FilterOnClass} Matches values that have the appropriate class.
     */
    private static final class FilterOnEquals<T> implements Filter {

      private final T value;

      FilterOnEquals(final T value) {
        this.value = value;
      }

      
      public final <V> boolean match(final V value) {
        if (this.value == null) {
          return value == null;
        }
        return this.value.equals(value);
      }
    }

    /**
     * {@code FilterAllOf} successfully matches a value if all of its children match it.
     */
    private static final class FilterAllOf extends FilterBase {

      FilterAllOf(final Filter... filters) {
        super(filters);
      }

      /**
       * Returns false if any one of the children failed to match the value, else true.
       */
      
      public final <V> boolean match(final V value) {
        for (final Filter child : this.children) {
          if (!child.match(value)) {
            return false;
          }
        }
        return true;
      }
    }

    /**
     * {@code FilterAnyOf} successfully matches a value if any one of its children matches it.
     */
    private static final class FilterAnyOf extends FilterBase {

      FilterAnyOf(final Filter... filters) {
        super(filters);
      }

      /**
       * Returns true if any one of the children matched the value, else false.
       */
      
      public final <V> boolean match(final V value) {
        for (final Filter child : this.children) {
          if (child.match(value)) {
            return true;
          }
        }
        return false;
      }
    }

    /**
     * {@code FilterNoneOf} successfully matches a value if none of its children match it.
     */
    private static final class FilterNoneOf extends FilterBase {

      FilterNoneOf(final Filter... filters) {
        super(filters);
      }

      /**
       * Returns true if any one of the children matched the value, else false.
       */
      
      public final <V> boolean match(final V value) {
        for (final Filter child : this.children) {
          if (child.match(value)) {
            return false;
          }
        }
        return true;
      }
    }
  }
}
