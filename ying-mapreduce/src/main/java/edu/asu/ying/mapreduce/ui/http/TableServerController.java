package edu.asu.ying.mapreduce.ui.http;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.asu.ying.mapreduce.TableServerDaemon;
import edu.asu.ying.mapreduce.rpc.net.NodeNotFoundException;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

/**
 * The {@link TableServerController} is a servlet that provides the interface with 
 * control over the {@link TableServerDaemon}, such as connecting to networks or
 * uploading files.
 */
public final class TableServerController 
	extends Controller {

	private final class ErrorResponseException extends RuntimeException {
		private final String response;
		
		public ErrorResponseException(final String response) {
			this.response = response;
		}
		
		public final String getResponse() { return this.response; }
	}
	
	private static final long serialVersionUID = -42609805888037945L;
	
	private final TableServerDaemon daemon;
	
	public TableServerController(final TableServerDaemon daemon) {
		this.daemon = daemon;
	}
	
	private void errorResponse(final Throwable cause) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<exception>");
			sb.append(cause.getClass().getSimpleName());
		sb.append("</exception>");
		sb.append("<detail>");
			sb.append(cause.getMessage());
		sb.append("</detail>");
		sb.append("<cause>");
			sb.append(cause.getCause().toString());
		sb.append("</cause>");
		
		cause.printStackTrace();
		
		throw new ErrorResponseException(sb.toString());
	}
	
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/xml");
		response.getWriter().write("<response>");
		
		try {
			// Get the action to take from the parameters
			final String method = request.getParameter("method");
			
			if (method == null || method.equals("")) {
				errorResponse(new IllegalArgumentException("Method name cannot be empty."));
			}
			response.getWriter().write("<method>".concat(method).concat("</method>"));
			
			final Method webMethod = this.actions.get(method);
			if (webMethod == null) {
				errorResponse(new UnsupportedOperationException());
			} else {
				try {
					webMethod.invoke(this, request, response);
				} catch (final Throwable e) {
					errorResponse(e);
				}
			}
		} catch (final ErrorResponseException e) {
			response.getWriter().write(e.getResponse());
		}
		
		response.getWriter().write("</response>");
		response.flushBuffer();
	}
	
	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		// For file uploads
		
	}
	
	@WebMethod(name = "connect")
	public void doConnect(final HttpServletRequest request, final HttpServletResponse response) 
			throws NodeNotFoundException, URISyntaxException, IOException {
		
		final URI uri = URI.create(request.getParameter("uri"));
		this.daemon.getSendChannel().join(uri);
		
		final Object remoteUri = this.daemon.getSendChannel().getTransportSink().getProperties().get("bootstrap-uri");
		if (remoteUri != null) {
			response.getWriter().write("<remote-addr>".concat(remoteUri.toString()).concat("</remote-addr>"));
		}
		response.getWriter().write("<success>true</success>");
	}
	
	@WebMethod(name = "nothing")
	public void doNothing(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final int rnd = (new Random()).nextInt();
		final String val = request.getParameter("value");
		if (val != null) {
			response.getWriter().write("<yousaid>".concat(val).concat("</yousaid>"));
		}
		response.getWriter().write("<value>".concat(String.valueOf(rnd)).concat("</value>"));
		response.getWriter().write("<success>true</success>");
	}
}
