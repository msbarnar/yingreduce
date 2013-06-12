package edu.asu.barnard.samples.rmi;

import java.io.Serializable;

public interface Mappable
	extends Serializable {
	
	public Object apply(final Object value);
}
