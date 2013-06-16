package edu.asu.ying.mapreduce.net.resource;

import java.rmi.RemoteException;


/**
 * Implements a {@link RemoteResource} that threw an exception in provision.
 */
public final class ResourceException
	implements RemoteResource
{
	private static final long SerialVersionUID = 1L;

	private final Throwable cause;

	public ResourceException(final Throwable cause) {
		this.cause = cause;
	}

	public final Throwable getCause() {
		return this.cause;
	}

	@Override
	public final ResourceIdentifier getResourceUri() throws RemoteException {
		return null;
	}
}
