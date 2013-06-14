package edu.asu.ying.mapreduce.rmi.resource;

import com.google.common.base.Optional;
import edu.asu.ying.mapreduce.messaging.MessageBase;


/**
 * {@link GetResourceResponse} is sent in response to {@link GetResourceMessage} and contains either a reference to the
 * resource or an exception.
 * <p>
 * The ID of this message will be the same as that of the {@link GetResourceMessage} that instigated it.
 * <p>
 * The following properties are defined by this message:
 * <ul>
 *     <li>{@code resource} - (nullable) the resource reference, if found.</li>
 *     <li>{@code exception} - (nullable) the exception if one was thrown.</li>
 * </ul>
 */
public final class GetResourceResponse
	extends MessageBase
{
	public GetResourceResponse(final String id) {
		this.setId(id);
	}
	/**
	 * Initializes the response with a resource reference.
	 * @param id the ID of the {@link GetResourceMessage} to which this is a response.
	 * @param resource the resource reference.
	 */
	public GetResourceResponse(final String id, final RemoteResource resource) {
		this.setId(id);
		this.setResource(resource);
	}
	/**
	 * Initializes an exceptional response.
	 * @param id the ID of the {@link GetResourceMessage} to which this is a response.
	 * @param exception the exception to return instead of the resource.
	 */
	public GetResourceResponse(final String id, final Throwable exception) {
		this.setId(id);
		this.setException(exception);
	}

	public final void setResource(final RemoteResource resource) { this.properties.put("resource", resource); }
	public final RemoteResource getResource() {
		return this.getNullableProperty("resource", RemoteResource.class);
	}
	public final void setException(final Throwable exception) { this.properties.put("exception", exception); }
	public final Throwable getException() {
		return this.getNullableProperty("exception", Throwable.class);
	}
}
