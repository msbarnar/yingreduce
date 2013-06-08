package edu.asu.ying.mapreduce.table;

import java.io.Serializable;
import java.util.AbstractMap;

public final class Element
	extends AbstractMap.SimpleEntry<Serializable, Serializable>
{
	private static final long serialVersionUID = -2111966900472883878L;
	
	public Element(final Serializable key) {
		super(key, null);
	}
	public Element(final Serializable key, final Serializable value) {
		super(key, value);
	}
}
