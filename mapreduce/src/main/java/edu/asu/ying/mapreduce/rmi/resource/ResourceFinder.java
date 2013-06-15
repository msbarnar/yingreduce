package edu.asu.ying.mapreduce.rmi.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


/**
 * The {@link ResourceFinder} facilitates the location and accession of {@link RemoteResource} objects by implementing the
 * interpretation of the {@link ResourceIdentifier} and communicating with the underlying network.
 */
public interface ResourceFinder
{
	/**
	 * Finds one or more {@link RemoteResource} matching the given {@link ResourceIdentifier} and returns their
	 * references.
	 * @param uri the identifier used to locate the resource.
	 * @return one or more remote references to matching resources, or null if no resource was found.
	 * @throws IOException if the underlying network accession throws an exception.
	 */
	RemoteResource findResource(final ResourceIdentifier uri) throws URISyntaxException, IOException;
}
