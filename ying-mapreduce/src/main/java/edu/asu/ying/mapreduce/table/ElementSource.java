package edu.asu.ying.mapreduce.table;

import java.io.IOException;
import java.util.NoSuchElementException;


/**
 * {@link ElementSource} objects read elements from a data stream and pass them to into an {@link ElementSink} chain.
 */
public interface ElementSource
{
	/**
	 * Return the next element sink in the chain.
	 */
	public ElementSink getNextSink();
	/**
	 * Reads the next element from the data source.
	 * <p>
	 * Blocks if no element is available until one becomes available or the underlying data source becomes unavailable.
	 * 
	 * @throws IOException if reading from the underlying data source throws an exception.
	 * @throws NoSuchElementException if no more elements are available or if the underlying data source closes or 
	 * becomes otherwise unavailable.
	 */
	public void readNextElement() throws IOException, NoSuchElementException;
	/**
	 * Reads the next element from the data source. 
	 * <p>
	 * If timeout is greater than 0, then the call will block for <code>timeout</code> milliseconds before throwing
	 * {@link NoSuchElementException}. 
	 * If timeout <= 0, the call will block until an element is available.
	 * 
	 * @param timeout if > 0, the call will block for <code>timeout</code> ms before throwing NoSuchElementException.
	 * If <= 0, the call will block until an element is available.
	 * 
	 * @throws IOException if reading from the underlying data source throws an exception.
	 * @throws NoSuchElementException if no more elements are available, <code>timeout</code> ms is exceeded before 
	 * any elements become available, or if the underlying data source closes or becomes otherwise unavailable.
	 */
	public void readNextElement(final int timeout) throws IOException, NoSuchElementException;
	/**
	 * Reads the next element from the data source.
	 * <p>
	 * Blocks if no element is available until one becomes available or the underlying data source becomes unavailable.
	 * 
	 * @param maxCount maximum number of elements to read.
	 * @throws IOException if reading from the underlying data source throws an exception.
	 * @throws NoSuchElementException if no more elements are available or if the underlying data source closes or 
	 * becomes otherwise unavailable.
	 */
	public void readNextElements(final int maxCount) throws IOException, NoSuchElementException;
	/**
	 * Reads the next element from the data source. 
	 * <p>
	 * If timeout is greater than 0, then the call will block for <code>timeout</code> milliseconds before throwing
	 * {@link NoSuchElementException}. 
	 * If timeout <= 0, the call will block until an element is available.
	 * 
	 * @param maxCount maximum number of elements to read.
	 * @param timeout if > 0, the call will block for <code>timeout</code> ms before throwing NoSuchElementException.
	 * If <= 0, the call will block until an element is available.
	 * 
	 * @throws IOException if reading from the underlying data source throws an exception.
	 * @throws NoSuchElementException if no more elements are available, <code>timeout</code> ms is exceeded before 
	 * any elements become available, or if the underlying data source closes or becomes otherwise unavailable.
	 */
	public void readNextElements(final int maxCount, final int timeout) throws IOException, NoSuchElementException;
}
