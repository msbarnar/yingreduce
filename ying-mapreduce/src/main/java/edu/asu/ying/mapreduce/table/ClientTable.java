package edu.asu.ying.mapreduce.table;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.asu.ying.mapreduce.events.EventArgs;
import edu.asu.ying.mapreduce.events.EventHandler;
import edu.asu.ying.mapreduce.rpc.messaging.*;
import edu.asu.ying.mapreduce.rpc.net.*;

/**
 * {@link ClientTable} is the heart of the P2P database on the client (issuing) side.
 * <p>
 * Individual key->value ({@link Element} objects) are added to the table, which
 * separates them into pages based on a predetermined number of elements per page.
 * <p>
 * Full pages are distributed across the network so that a complete table exists
 * only as the sum of all of the pages on the network holding that table's ID.
 * <p>
 * The table object is a proxy to the distributed tables with regards to retrieval.
 * Requesting a key from the table ({@link Map#get}) initiates a lookup for the page
 * holding that key. When the appropriate page is found, the value associated with the key
 * is requested from the node holding that page.
 * <p>
 * The table is therefore a lazily-populated hash table wherein each bucket exists
 * on a separate physical node.
 * <p>
 * A distributed filesystem could be implemented by creating a table with a page size of
 * 1, wherein each key is a filename and each value is its contents. Each file would then
 * be distributed to a different node.
 * 
 * @see Page
 * @see Element
 */
public final class ClientTable
	implements Map<Serializable, Serializable>, Distributable, ElementSink, Serializable
{
	private static final long serialVersionUID = 578130745143581146L;
	
	/*****************************************************************
	 * Serialized													 */
	// Network-wide table name
	private final TableID id;
	// Distributable key
	private final NetworkKey networkKey;
	// Number of elements per page
	private int pageSize = 1;
	// Number of pages to keep in memory
	private int maxPageQueueSize = 1;
	// The total number of pages on the table, including those that have been paged out.
	private int numPages = 0;
	/*****************************************************************/
	
	// The sink that will handle page out messages from this table
	private final transient MessageSink nextSink;
	// The actual pages making up the table.
	// The current, most recent, least full page is the tail of the queue.
	private final transient Queue<Page> pageQueue = new ConcurrentLinkedQueue<Page>();
	// The page we add elements to and listen to page full events from
	private transient Page currentPage;
	
	
	/**
	 * Initialize an empty table that sinks its page out messages to <code>nextSink</code>.
	 * @param id the network-wide unique table identifier
	 * @param nextSink the sink that will receive page out messages
	 */
	public ClientTable(final TableID id, final MessageSink nextSink) {
		// The next sink receives pages on page out.
		this.nextSink = nextSink;
		// The table is identified on the network by its id.
		this.id = id;
		this.networkKey = new NetworkKey().add(this.id);
		// Start the table with a single blank page
		this.newPage();
	}
	
	public final TableID getTableId() { return this.id; }
	
	/**
	 * Adds a new empty page to the queue, triggering a page out if the number of
	 * queued pages exceeds the maximum allowed.
	 */
	private void newPage() {
		this.currentPage = new Page(this, this.numPages, this.pageSize);
		// Add a new page every time a page fills up
		this.currentPage.onPageFull.attach(new EventHandler<EventArgs>() {
			@Override
			public void onEvent(final Object sender, final EventArgs args) {
				ClientTable.this.newPage();
				// Only listen to page full events from the most recent page
				//FIXME: ConcurrentModification; we're iterating the event right now!
				// FIXME: ((Page) sender).onPageFull.detach(this);
			}
		});
		this.pageQueue.add(this.currentPage);
		this.numPages++;
		
		// Page out if we have too many pages.
		if (this.numPages > this.maxPageQueueSize) {
			this.pageOut();
		}
	}
	
	/**
	 * Distribute all pages.
	 */
	public void flushPages() {
		while (!this.pageQueue.isEmpty()) {
			this.pageOut(this.pageQueue.poll());
		}
	}
	
	/**
	 * Distribute full pages in descending order of their age.
	 */
	private void pageOut() {
		while (this.pageQueue.peek().isFull()) {
			final Page fullPage = this.pageQueue.poll();
			if (!fullPage.isDistributed()) {
				this.pageOut(fullPage);
			}
		}
	}
	
	/**
	 * Package a page in a {@link PageOutRequest} and send it to the next sink.
	 * @param page the page that will be passed down the sink chain
	 */
	private void pageOut(final Page page) {
		// TODO: This needs to be asynchronous
		// Necessary headers such as the network key are set automatically
		final PageOutRequest msg = new PageOutRequest(page);
		// Send the message down the chain and get the result synchronously
		final PageOutResponse response = (PageOutResponse) this.nextSink.processMessage(msg);
		//TODO: page.setIsDistributed((Boolean) response.getStatus());
	}
	
	/*****************************************************************
	 * ElementSink implementation 									 */
	
	/**
	 * Add a single element to the table.
	 * <p>
	 * Potentially triggers a network distribution of the table.
	 */
	@Override
	public void processElement(final Element element) {
		try {
			// This may fire an onPageFull event, which will start a new page
			this.currentPage.put(element);
		} catch (PageFullException e) {
			// We should have a new page, but don't likely due to a synchronization problem.
			// FIXME: Not thread safe: another thread can fill the page between onPageFull
			// triggering a new page and us trying to write to it
			throw new AssertionError("Tried adding to a full page; table probably out of sync.", e);
		}
	}

	/**
	 * Add a collection of elements to the table.
	 * <p>
	 * Potentially triggers a network distribution of the table.
	 */
	@Override
	public void processElements(final List<Element> elements) {
		// Recursively add pages and add overflowed elements to them
		// until all of the elements are in the table.
		try {
			// This may fire an onPageFull event, starting a new page
			this.currentPage.putAll(elements);
		} catch (PageFullException e) {
			// The onPageFull event should have started a new page, so we can just
			// continue adding the overflow recursively.
			this.processElements(e.getOverflow());
		}
	}

	/**
	 * {@link ClientTable} is the end of the element sink chain.
	 * @return null
	 */
	@Override
	public ElementSink getNextElementSink() {
		return null;
	}

	/*****************************************************************
	 * MessageSink implementation 									 */
	
	/**
	 * Returns the message sink that receives {@link PageOutRequest} objects from the table.
	 */
	public MessageSink getNextSink() {
		return this.nextSink;
	}

	/*****************************************************************
	 * Distributable implementation 								 */
	
	/**
	 * Returns a network key based on the table's id.
	 */
	@Override
	public NetworkKey getNetworkKey() { return this.networkKey; }

	/*****************************************************************
	 * Map implementation 											 */
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Serializable get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Serializable put(Serializable key, Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Serializable remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void putAll(Map<? extends Serializable, ? extends Serializable> m) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Set<Serializable> keySet() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Collection<Serializable> values() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<java.util.Map.Entry<Serializable, Serializable>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
}
