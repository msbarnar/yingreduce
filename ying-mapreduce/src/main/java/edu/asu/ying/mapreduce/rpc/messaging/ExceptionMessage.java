package edu.asu.ying.mapreduce.rpc.messaging;

public final class ExceptionMessage
	extends MessageBase
{
	private static final long serialVersionUID = 8976466993078417606L;
	
	private final Throwable cause;
	
	public ExceptionMessage(final Throwable cause) {
		super();
		this.cause = cause;
	}
	
	public final Throwable getCause() { return this.cause; }
}
