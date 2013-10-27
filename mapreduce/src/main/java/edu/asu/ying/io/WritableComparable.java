package edu.asu.ying.io;

/**
 * {@code WritableComparable} extends the {@link Writable} interface by enforcing comparability.
 */
public interface WritableComparable<T> extends Writable, Comparable<T> {

}
