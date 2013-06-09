package edu.asu.ying.mapreduce.ui.http;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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
		final List<ObservableProperties> propList = this.connectTo.getExposedProps();
		final StringBuilder sb = new StringBuilder();
		
		// Build an XML document from the properties
		// <node>
		// 	<MyObject>
		//    <MyProperty>stuff</MyProperty>
		//  </MyObject>
		// </node>
		sb.append("<node>");
		for (final ObservableProperties props : propList) {
			sb.append(this.openTag(props.getClassName()));
			for (final Map.Entry<String, Serializable> entry : props) {
				sb.append(this.openTag(entry.getKey()));
				sb.append(entry.getValue());
				sb.append(closeTag(entry.getKey()));
			}
			sb.append(this.closeTag(props.getClassName()));
		}
		sb.append("</node>");
		
		return sb.toString();
	}
	
	private final String openTag(final String name) {
		return "<".concat(StringEscapeUtils.escapeXml(name)).concat(">");
	}
	private final String closeTag(final String name) {
		return "</".concat(StringEscapeUtils.escapeXml(name)).concat(">");
	}
}
