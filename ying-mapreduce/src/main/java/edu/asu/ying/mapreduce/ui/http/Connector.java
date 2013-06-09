package edu.asu.ying.mapreduce.ui.http;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.lang3.StringEscapeUtils;

import edu.asu.ying.mapreduce.TableServerDaemon;
import edu.asu.ying.mapreduce.ui.Observable;
import edu.asu.ying.mapreduce.ui.ObservableProperties;
import edu.asu.ying.mapreduce.ui.ObservableProvider;

/**
 * Exposes information about the node found at the key specified in the url 
 */
public final class Connector
	extends HttpServlet
{
	private static final long serialVersionUID = -545603243684031979L;
	
	private final Observable connectTo;
	
	public Connector(final Observable connectTo) {
		// Get an instance of the table server so we can observe it
		this.connectTo = connectTo;
	}
	/**
	 * Default response is the status of the node:
	 * <p>
	 * <pre>
	 * {@code
	 * <node>
	 * 	<status>error</status>
	 * 	<message>RuntimeException</message>
	 * </node>
	 * }
	 * </pre>
	 */
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		final String resp = this.getResponse();
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentLength(resp.length());
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(resp);
		response.flushBuffer();
	}
	
	private final String getResponse() {
		final ObservableProperties props = this.connectTo.getExposedProps();
		final StringBuilder sb = new StringBuilder();
		
		// Build an XML document from the properties
		sb.append("<node>");
		for (final Map.Entry<String, Serializable> entry : props) {
			// open tag
			sb.append('<');
				sb.append(entry.getKey());
			sb.append('>');
				sb.append(entry.getValue());
			// close tag
			sb.append("</");
				sb.append(entry.getKey());
			sb.append('>');
		}
		sb.append("</node>");
		
		return sb.toString();
	}
}
