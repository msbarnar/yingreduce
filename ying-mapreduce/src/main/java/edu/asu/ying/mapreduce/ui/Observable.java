package edu.asu.ying.mapreduce.ui;

import java.util.List;

/**
 * Marks a class as exposing properties for observation.
 */
public interface Observable {
	public List<ObservableProperties> getExposedProps();
}
