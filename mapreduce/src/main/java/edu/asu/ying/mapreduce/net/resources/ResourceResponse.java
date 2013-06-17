package edu.asu.ying.mapreduce.net.resources;

import com.google.common.base.Preconditions;
import edu.asu.ying.mapreduce.messaging.Message;

import java.net.URISyntaxException;


/**
 * {@link ResourceResponse} is sent in response to {@link ResourceRequest} and contains either a reference to the
 * resources or an exception.
 * <p>
 * The ID of this message will be the same as that of the {@link ResourceRequest} that instigated it.
 * <p>
 * The following properties are defined on this message:
 * <ul>
 *     <li>{@code resources.reference} - (optional) the resources reference, if found.</li>
 *     <li>{@code throwable} - (optional) the exception if one was thrown.</li>
 * </ul>
 */
public final class ResourceResponse
	extends ResourceMessage
{
	public static ResourceResponse inResponseTo(final Message request)
			throws URISyntaxException {

		return (ResourceResponse) new ResourceResponse().makeResponseTo(request);
	}
	public static ResourceResponse inResponseTo(final Message request, final RemoteResource resource)
			throws URISyntaxException {

		return (ResourceResponse) new ResourceResponse(resource).makeResponseTo(request);
	}
	public static ResourceResponse inResponseTo(final Message request, final Throwable throwable)
			throws URISyntaxException {

		return (ResourceResponse) new ResourceResponse(throwable).makeResponseTo(request);
	}

	public static final class Property {
		public static final String ResourceInstance = "resources.instance.ref";
	}

	private ResourceResponse()
			throws URISyntaxException {
		// Responses should only go to the node that sent the request
		this.setReplication(1);
	}
	/**
	 * Initializes the response with a resources reference.
	 * @param resource the resources reference.
	 */
	private ResourceResponse(final RemoteResource resource)
			throws URISyntaxException {

		this();
		this.setResourceInstance(resource);
	}
	/**
	 * Initializes an exceptional response.
	 * @param throwable the throwable to return instead of the resources.
	 */
	private ResourceResponse(final Throwable throwable)
			throws URISyntaxException {

		this();
		Preconditions.checkNotNull(throwable);
		this.setException(throwable);
	}

	public final void setResourceInstance(final RemoteResource resource) {
		Preconditions.checkNotNull(resource);
		this.properties.put(Property.ResourceInstance, resource);
	}
	public final RemoteResource getResourceInstance() {
		return this.properties.getDynamicCast(Property.ResourceInstance, RemoteResource.class);
	}
}
