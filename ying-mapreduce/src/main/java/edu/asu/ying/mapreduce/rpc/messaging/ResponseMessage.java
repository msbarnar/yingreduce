package edu.asu.ying.mapreduce.rpc.messaging;

import java.io.Serializable;

/**
 * Base class for a response to a {@link RequestMessage}.
 */
public class ResponseMessage
	extends MessageBase
{
	private static final long serialVersionUID = -1389848282690757462L;

	public ResponseMessage(final Message request) {
		this.setNetworkKey(request.getNetworkKey());
	}
	public ResponseMessage(final Message request, final Throwable exception) {
		this(request);
		this.setException(exception);
	}
	public ResponseMessage(final Message request, final Serializable status) {
		this(request);
		this.setStatus(status);
	}
	
	protected void setRequest(final Message request) {
		this.properties.put("request", request);
	}
	public Message getRequest() {
		return (Message) this.properties.get("request");
	}
	
	protected void setException(final Throwable exception) {
		this.properties.put("exception", new ExceptionMessage(exception));
	}
	public Throwable getException() {
		return ((ExceptionMessage) this.properties.get("exception")).getCause();
	}
	
	protected void setStatus(final Serializable status) {
		this.properties.put("status", status);
	}
	public Serializable getStatus() {
		return this.properties.get("status");
	}
}
