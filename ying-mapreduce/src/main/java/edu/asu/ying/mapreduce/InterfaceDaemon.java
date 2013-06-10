package edu.asu.ying.mapreduce;

import java.util.logging.Level;

import edu.asu.ying.mapreduce.logging.Logger;
import edu.asu.ying.mapreduce.ui.http.HttpUIServer;

/**
 * Manages the web server that serves the user interface.
 */
public final class InterfaceDaemon
{
	private final HttpUIServer server;
	
	public InterfaceDaemon() throws Exception {
		try {
			Logger.get().info("Starting interface daemon");
			this.server = new HttpUIServer();
			Logger.get().info("Interface is running on port ".concat(String.valueOf(this.server.getProperties().get("port"))));
		} catch (final Exception e) {
			Logger.get().log(Level.SEVERE, 
					"Web interface daemon failed to start due to an exception",
					e);
			throw e;
		}
	}
	
	public void stop() throws Exception {
		this.server.stop();
	}
}
