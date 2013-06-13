package edu.asu.ying.mapreduce.net;

import java.net.URI;


/**
 * A {@link RemoteResource} is a reference to a resource that exists outside the current context and can be discovered,
 * accessed, or referenced using a {@link java.net.URI}.
 * <p>
 * Required parts of a resource URI are:
 * <ul>
 *     <li>{@code scheme} - describes the type of resource and is not strictly defined.</li>
 *     <li>{@code host} - describes the location information for the resource; its meaning is implementation specific.</li>
 *     <li>{@code path} - names the resource, if necessary.</li>
 * </ul>
 * <p>
 * Optional parts:
 * <ul>
 *     <li>{@code authority} - specifies authorization parameters for resources that are access-controlled.</li>
 * </ul>
 */
public interface RemoteResource
{
	public URI getUri();
	public Object getResource();
}
