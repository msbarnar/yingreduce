package edu.asu.ying.mapreduce.table;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import edu.asu.ying.mapreduce.events.*;
import edu.asu.ying.mapreduce.rpc.net.Distributable;
import edu.asu.ying.mapreduce.rpc.net.NetworkKey;


/**
 * Contains a single page of {@link WritableValue} elements from a {@link ClientTable}.
 * Pages are the unit of exchange of data across the DHT network, and are the input
 * types for map and reduce operations. A single page therefore contains all of the data that
 * will be processed by a single node during a map operation, though a node can be responsible
 * for multiple pages. 
 */
public final class Page 
	extends ConcurrentHashMap<Serializable, Serializable>
	implements Distributable, Serializable
{
	private static final long serialVersionUID = 4202329486869835943L;
	
	/* Serializable
	 * --------------------------------- */
	// The ID of the table this page is from
	private final TableID tableId;
	// The index of this page on the table
	private final int index;
	// The maximum number of elements to accept before firing {@link Page#onPageFull}.
	private final int maxSize = 1;
	// The unique key identifying this page on the network
	// Page keys are constructed from the table ID + page index, so all
	// pages of a particular table can be located if its ID is known.
	private final NetworkKey key;
	/* --------------------------------- */
	 
	// The table this page is from
	private final transient ClientTable table;
	// True if the page has already been distributed to the network
	private transient boolean isDistributed = false;
	/**
	 * Fired once when the page first reaches its maximum capacity.
	 * <p>
	 * Further attempts to add elements after this has fired will throw an exception.
	 */
	public final transient Event onPageFull = new Event();
	
	/**
	 * Initialize a blank page from a table's parameters.
	 * @param table the parent table holding this page.
	 * @param index the index of this page on its parent table.
	 * @param maxSize the capacity of this table at which it will fire {@link Page#onPageFull}
	 */
	public Page(final ClientTable table, final int index, final int maxSize) {
		this.table = table;
		this.tableId = table.getTableId();
		this.index = index;
		//this.maxSize = maxSize;
		this.key = this.makeKey();
	}
	
	/**
	 * Composes a {@link NetworkKey} uniquely identifying the page given its parent
	 * table and index.
	 */
	private final NetworkKey makeKey() {
		return new NetworkKey().add(this.table.getNetworkKey()).add(this.index);
	}
	public final NetworkKey getNetworkKey() { return this.key; }
	
	/**
	 * Attempt to add an element to the page.
	 * 
	 * @throw PageFullException if the page is already full. 
	 */
	public Serializable put(final Element element) {
		// Only add new elements if the page is not full
		final int size = this.size();
		if (!this.isFull()) { 
			final Serializable oldVal = super.put(element.getKey(), element.getValue());
			// Only fire onPageFull the first time the page becomes full.
			// Subsequent attempts to add elements will throw an exception.
			if (this.isFull()) {
				this.onPageFull.fire(this, EventArgs.EMPTY);
			}
			return oldVal;
		} else {
			throw new PageFullException(new ArrayList<Element>(Arrays.asList(new Element((Serializable) element.getKey(), (Serializable) element.getValue()))));
		}
	}
	/**
	 * Put an element without checking for overflow or throwing exceptions.
	 */
	private Serializable putBlindly(final Serializable key, final Serializable value) {
		return super.put(key, value);
	}
	
	/**
	 * Attempt to add a collection of elements to the page.
	 * 
	 * @throw PageFullException if the page cannot hold all of the elements. The remainder are
	 * returned in the exception.
	 */
	public void putAll(final List<Element> elements) {
		final Stack<Element> overflow = new Stack<Element>();
		// Keep track of how close we are to maxSize
		int size = this.size();
		for (final Element element : elements) {
			// Put elements that fit in the page and elements that don't in overflow
			if (size < this.maxSize) {
				this.putBlindly(element.getKey(), element.getValue());
				size++;
			} else {
				overflow.push(element);
			}
		}
		
		// Send overflow back to the caller
		if (overflow.size() > 0) {
			throw new PageFullException(overflow);
		}
	}
	/**
	 * Attempt to add a collection of elements to the page.
	 * 
	 * @throw PageFullException if the page cannot hold all of the elements. The remainder are
	 * returned in the exception.
	 */
	@Override
	public void putAll(final Map<? extends Serializable, ? extends Serializable> elements) {
		final Stack<Element> overflow = new Stack<Element>();
		// Keep track of how close we are to maxSize
		int size = this.size();
		for (final Map.Entry<? extends Serializable, ? extends Serializable> entry : elements.entrySet()) {
			// Put elements that fit in the page and elements that don't in overflow
			if (size < this.maxSize) {
				// Don't throw any exceptions or fire events
				this.putBlindly(entry.getKey(), entry.getValue());
				size++;
			} else {
				overflow.push((Element) entry);
			}
		}
		
		// Send overflow back to the caller
		if (overflow.size() > 0) {
			throw new PageFullException(overflow);
		}
	}
	
	public final TableID getTableId() { return this.tableId; }
	public boolean isFull() { return this.size() >= this.maxSize; }
	
	public void setIsDistributed(final boolean value) { this.isDistributed = value; }
	/**
	 * Returns true if the page has been distributed to the network.
	 */
	public boolean isDistributed() { return this.isDistributed; }
}
