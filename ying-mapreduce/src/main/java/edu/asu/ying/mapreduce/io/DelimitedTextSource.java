package edu.asu.ying.mapreduce.io;

import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;

import edu.asu.ying.mapreduce.table.Element;
import edu.asu.ying.mapreduce.table.ElementSink;
import edu.asu.ying.mapreduce.table.ElementSource;

/**
 * Provides element pairs from a text source.
 * 
 * Elements are separated by an element delimiter, and key->value pairs
 * are separated by a pair delimiter.
 */
public class DelimitedTextSource
	implements ElementSource
{
	protected final BufferedReader reader;
	protected final ElementSink nextSink;
	
	/**
	 * Open the source on an input and/or output stream.
	 * 
	 * Elements are delimited by <code>elementDelimiter</code>, and key->value pairs within each
	 * element are delimited by <code>pairDelimiter</code>.
	 * <p>
	 * E.g. if <code>elementDelimiter</code> is <code>\n</code> and <code>pairDelimiter</code> is <code>,</code>
	 * then the following text:
	 * <pre>
	 * {@code
	 * Apples,Red
	 * Lemons,Yellow}</pre>
	 * would produce the following elements:
	 * <pre>
	 * {@code
	 * ["Apples"->"Red", "Lemons"->"Yellow"]}</pre>
	 * 
	 * @param input the text input stream.
	 * @param elementDelimiter the delimiter that separates one key->value pair from another.
	 * @param pairDelimiter the delimiter that separates the key from the value in each element.
	 */
	public DelimitedTextSource(final InputStream input, final ElementSink nextSink, 
	                           final char elementDelimiter, final char pairDelimiter) {
		this.reader = new BufferedReader(new InputStreamReader(input));
		this.nextSink = nextSink;
	}

	/**
	 * Pass the element to each of the element sinks in the sink stack.
	 */
	private void processElement(final Element element) {
		this.nextSink.processElement(element);
	}
	/**
	 * Pass all of the elements to each of the element sinks in the sink stack.
	 */
	private void processElements(final List<Element> elements) {
		this.nextSink.processElements(elements);
	}
	
	/**
	 * Returns the stack of element sinks that will receieve elements produced by this element source.
	 */
	@Override
	public ElementSink getNextSink() {
		return this.nextSink;
	}

	/**
	 * Retrieve an {@link Element} from the underlying data source and send it to the sink stack.
	 * 
	 * @throw NoSuchElementException if there are no more elements available from the data source.
	 */
	@Override
	public void readNextElement() throws IOException, NoSuchElementException {
		final String line;
		try {
			line = this.reader.readLine();
			if (line == null) {
				throw new NoSuchElementException();
			}
		} catch (final IOException e) {
			throw new NoSuchElementException();
		}
		
		final String[] parts = line.split("=");
		if (parts.length == 2) {
			final Element element = new Element(parts[0], parts[1]);
			this.processElement(element);
		}
	}

	/**
	 * Retrieve an {@link Element} from the underlying data source and send it to the sink stack.
	 * 
	 * @throw NoSuchElementException if there are no more elements available from the data source within
	 * <code>timeout</code> milliseconds.
	 */
	@Override
	public void readNextElement(int timeout) throws IOException, NoSuchElementException {
		final Element element = null;
		this.processElement(element);
	}

	/**
	 * Retrieve at most <code>maxCount</code> elements from the underlying data source and send them to the sink stack.
	 * 
	 * @throw NoSuchElementException if there are no more elements available from the data source.
	 */
	@Override
	public void readNextElements(int maxCount) throws IOException, NoSuchElementException {
		final List<Element> elements = null;
		this.processElements(elements);
	}

	/**
	 * Retrieve at most <code>maxCount</code> elements from the underlying data source and send them to the sink stack.
	 * 
	 * @throw NoSuchElementException if there are no more elements available from the data source within
	 * <code>timeout</code> milliseconds.
	 */
	@Override
	public void readNextElements(int maxCount, int timeout) throws IOException, NoSuchElementException {
		final List<Element> elements = null;
		this.processElements(elements);
	}
}
