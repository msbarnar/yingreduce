package edu.asu.ying.mapreduce.net.resources;

import com.google.common.base.Preconditions;

import java.rmi.RemoteException;


/**
 * Implements a {@link RemoteResource} that threw an exception in provision.
 */
public final class ResourceException
	implements RemoteResource
{
	private static final long SerialVersionUID = 1L;

	private final Throwable cause;
	private final ResourceIdentifier resourceUri;

	public ResourceException(final ResourceIdentifier resourceUri, final Throwable cause) {
		Preconditions.checkNotNull(resourceUri);
		Preconditions.checkNotNull(cause);

		this.resourceUri = resourceUri;
		this.cause = cause;
	}
	public ResourceException(final Throwable cause) {
		this(ResourceIdentifier.Empty, cause);
	}

	public final Throwable getCause() {
		return this.cause;
	}

	@Override
	public final ResourceIdentifier getResourceUri() throws RemoteException {
		return this.resourceUri;
	}
}
