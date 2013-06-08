package edu.asu.ying.mapreduce.table;

import java.util.List;


/**
 * {@link ElementSink} objects sink elements and pass them to an element sink chain.
 */
public interface ElementSink
{
	public ElementSink getNextElementSink();
	public void processElement(final Element element);
	public void processElements(final List<Element> elements);
}
