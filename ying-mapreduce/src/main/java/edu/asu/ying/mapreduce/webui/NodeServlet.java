package edu.asu.ying.mapreduce.webui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * Exposes information about the node found at the key specified in the url 
 */
public final class NodeServlet
	extends HttpServlet
{
	private static final long serialVersionUID = -545603243684031979L;
	
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		final String resp = "<status>Running</status>";
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentLength(resp.length());
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(resp);
		response.flushBuffer();
	}
}
