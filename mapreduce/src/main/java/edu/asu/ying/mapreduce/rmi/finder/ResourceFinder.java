package edu.asu.ying.mapreduce.rmi.finder;

import com.google.common.util.concurrent.ListenableFuture;
import edu.asu.ying.mapreduce.net.RemoteResource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


/**
 * The {@link ResourceFinder} facilitates the location and accession of {@link RemoteResource} objects by implementing the
 * interpretation of the {@link java.net.URI} and communicating with the underlying network.
 */
public interface ResourceFinder
{
	/**
	 * Finds one or more {@link RemoteResource} matching the given {@link URI} and returns their references.
	 * @param uri the identifier used to locate the resource.
	 * @return one or more remote references to matching resources, or null if no resource was found.
	 * @throws URISyntaxException if the {@link URI} is not in the proper format.
	 * @throws IOException if the underlying network accession throws an exception.
	 */
	public List<RemoteResource> findResource(final URI uri) throws URISyntaxException, IOException;
}
