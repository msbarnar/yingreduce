package edu.asu.ying.mapreduce.table;

import java.util.*;

/**
 * {@link PageFullException} is thrown when too many elements are added to a page.
 * 
 * Elements that did not fit on the page are returned in the exception.
 */
public final class PageFullException
	extends RuntimeException
{
	private static final long serialVersionUID = -9107303083097901083L;
	
	private final List<Element> overflow;
	
	public PageFullException() {
		this.overflow = null;
	}
	
	public PageFullException(final Element overflow) {
		this.overflow = new ArrayList<Element>(1);
		this.overflow.add(overflow);
	}
	
	public PageFullException(final List<Element> overflow) {
		this.overflow = overflow;
	}
	
	/**
	 * Returns a list of the elements that did not fit on the page.
	 */
	public List<Element> getOverflow() { return this.overflow; }
}
