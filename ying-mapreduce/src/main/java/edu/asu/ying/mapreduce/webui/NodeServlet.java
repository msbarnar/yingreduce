package edu.asu.ying.mapreduce.webui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Exposes information about the node found at the key specified in the url 
 */
public final class NodeServlet
	extends HttpServlet
{
	private static final long serialVersionUID = -545603243684031979L;
	
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
		//final String resp = "<node><status>error</status><message>RuntimeException: Don't know what I'm doing!</message></node>";
		//final String resp = "<node><status>down</status></node>";
		final String resp = this.getNodeStatus();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentLength(resp.length());
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(resp);
		response.flushBuffer();
	}
	
	private final String getNodeStatus() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<node><status>");
		sb.append("running");
		sb.append("</status></node>");
		return sb.toString();
	}
}
