/*
 * Logger.java
 * Logger factory using daemon configuration
 */
package edu.asu.ying.mapreduce.logging;


public class Logger
{
	private static java.util.logging.Logger LOG;
	
	// Return the requested logger with settings from configuration
	public static final java.util.logging.Logger get() {
		if (LOG == null) {
			LOG = java.util.logging.Logger.getLogger(Logger.class.getName());
		}
		return LOG;
	}
}
