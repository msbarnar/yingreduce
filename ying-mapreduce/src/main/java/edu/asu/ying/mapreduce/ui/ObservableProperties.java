package edu.asu.ying.mapreduce.ui;

import java.io.Serializable;
import java.util.*;

public final class ObservableProperties 
	extends ArrayList<Map.Entry<String, Serializable>> 
{
	private static final long serialVersionUID = -5594111211722963721L;
	
	private final String owner;
	
	public ObservableProperties(final Object owner) {
		this.owner = owner.getClass().getSimpleName();
	}
	
	public final String getClassName() { return this.owner; }
}
