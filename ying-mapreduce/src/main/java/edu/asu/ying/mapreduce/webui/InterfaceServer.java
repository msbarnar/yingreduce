package edu.asu.ying.mapreduce.webui;

import java.util.*;

import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 * The base HTTP server for the web interface.
 */
public class InterfaceServer
{
	private final Server httpServer;
	// Manages the delegation to servlets
	private final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	// Serves default page
	private final ResourceHandler resources = new ResourceHandler();
	
	private final Map<String, Object> properties = new HashMap<String, Object>();
	
	
	public InterfaceServer() throws Exception {
		// TODO: Use configuration
		this.properties.put("port", 8777);
		this.httpServer = new Server(Integer.parseInt(this.properties.get("port").toString()));
		this.bind();
	}
	
	private void bind() throws Exception {
		this.resources.setDirectoriesListed(false);
		// Serve this page if no other handler matches the request
		// TODO: Use configuration
		this.resources.setWelcomeFiles(new String[] { "index.html" });
		// Default HTML folder
		this.resources.setResourceBase("www");
		
		// Connect servlets to associated URLs
		this.context.setContextPath("/node");
		this.context.addServlet(new ServletHolder(new NodeServlet()), "/");
		
		// Attach handlers to server
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resources, this.context });
		this.httpServer.setHandler(handlers);
		
		// throws Exception on failure, caught by daemon
		this.httpServer.start();
	}
	
	public void stop() throws Exception {
		this.httpServer.stop();
		this.httpServer.join();
	}
	
	public final Map<String, Object> getProperties() { return this.properties; }
}
