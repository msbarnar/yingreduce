package edu.asu.ying.mapreduce.messaging.filter2;

/**
 * A {@code FilterNode} is a node in the tree of filters.
 */
public interface FilterNode
{
	/**
	 * Maps the value to the node's children and reduces their results.
	 */
	<V> boolean match(final V value);
}
