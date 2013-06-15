package edu.asu.ying.mapreduce.rmi.resource;

import com.google.common.base.Optional;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.MessageBase;

import java.net.URISyntaxException;


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
	public GetResourceResponse(final Message request) {
		super(request.getSourceUri());
		this.setId(request.getId());
	}
	/**
	 * Initializes the response with a resource reference.
	 * @param request the message to which this is a response.
	 * @param resource the resource reference.
	 */
	public GetResourceResponse(final Message request, final RemoteResource resource) {
		this(request);
		this.setResource(resource);
	}
	/**
	 * Initializes an exceptional response.
	 * @param request the message to which this is a response.
	 * @param exception the exception to return instead of the resource.
	 */
	public GetResourceResponse(final Message request, final Throwable exception) {
		this(request);
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
