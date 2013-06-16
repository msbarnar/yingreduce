package edu.asu.ying.mapreduce.rmi.resource;

import java.rmi.RemoteException;


/**
 * Implements a {@link RemoteResource} that threw an exception in provision.
 */
public final class ResourceException
	implements RemoteResource
{
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
