package edu.asu.ying.mapreduce.rpc.messaging;

/**
 * Signals that a {@link Message} received by a {@link MessageSink} was not valid for
 * that sink or contained invalid data or properties.
 */
public final class InvalidMessageException
	extends RuntimeException
{
	private static final long serialVersionUID = -8356684982074387431L;
	
	private final Message message;
	
	public InvalidMessageException(final Message message) {
		super();
		this.message = message;
	}
	public InvalidMessageException(final Message message, final Throwable cause) {
		super(cause);
		this.message = message;
	}
	public InvalidMessageException(final Message message, final String detail, final Throwable cause) {
		super(detail, cause);
		this.message = message;
	}
	
	// Throwable stole my method name
	/**
	 * Returns the message that caused the exception.
	 */
	public final Message getCausativeMessage() { return this.message; }
}
