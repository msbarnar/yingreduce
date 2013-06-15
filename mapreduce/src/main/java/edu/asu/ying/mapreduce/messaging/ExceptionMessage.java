package edu.asu.ying.mapreduce.messaging;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.rmi.RemoteException;


/**
 * {@link ExceptionMessage} wraps a {@link java.lang.Throwable} in a message for responding to requests.
 */
public class ExceptionMessage
	extends MessageBase
{
	private final static long serialVersionUID = 1L;

	public ExceptionMessage(final Throwable cause) {
		super();
		this.setCause(cause);
	}

	public ExceptionMessage(final Throwable cause, final String detail) {
		super();
		this.setCause(cause);
		this.setDetail(detail);
	}

	protected void setCause(final Throwable cause) { this.properties.put("exception-cause", cause); }

	/**
	 * @return a {@link RemoteException} wrapping the {@link java.lang.Throwable} that was thrown on the remote host.
	 */
	public RemoteException getException() {
		final Optional<Serializable> cause = Optional.fromNullable(this.properties.get("exception-cause"));
		if (!cause.isPresent()) {
			return new RemoteException("Cause not available");
		}
		if (!(cause.get() instanceof Throwable)) {
			return new RemoteException(String.valueOf(cause.get()));
		}

		final Optional<String> detail = Optional.fromNullable(Strings.emptyToNull(this.getDetail()));
		if (detail.isPresent()) {
			return new RemoteException(detail.get(), (Throwable) cause.get());
		} else {
			return new RemoteException("Remote host threw an exception", (Throwable) cause.get());
		}
	}

	protected void setDetail(final String detail) { this.properties.put("exception-detail", detail); }
	public String getDetail() { return String.valueOf(this.properties.get("exception-detail")); }
}
