package edu.asu.barnard.samples.rmi;

import java.io.Serializable;

public class MapResult 
	implements Serializable {

	private static final long serialVersionUID = 8445732904448233161L;
	
	private final Object value;
	
	public MapResult(final Object value) {
		this.value = value;
	}
	
	public Object getValue() { return this.value; }
}
