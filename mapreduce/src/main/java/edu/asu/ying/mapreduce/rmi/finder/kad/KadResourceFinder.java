package edu.asu.ying.mapreduce.rmi.finder.kad;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import edu.asu.ying.mapreduce.messaging.MessageSink;
import edu.asu.ying.mapreduce.net.RemoteResource;
import edu.asu.ying.mapreduce.rmi.finder.ResourceFinder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


/**
 * The {@link KadResourceFinder} locates {@link RemoteResource} objects on a Kademlia network and returns
 * their references.
 * <p>
 * The types of resources located, organized by the {@link java.net.URI} {@code scheme} part are:
 * <ul>
 *     <li>{@code activator}: a {@link java.rmi.Remote} object activator that returns remote object references.</li>
 * </ul>
 */
public final class KadResourceFinder
	implements ResourceFinder
{
	@Inject
	public KadResourceFinder(final @Named("rmi.finder.ResourceFinder.sendSink") MessageSink sendSink) {
	}

	/**
	 * Constructs a {@link GetResourceMessage} with the resource identifier and node key in the URI.
	 * @param uri the identifier used to locate the resource.
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Override
	public final List<RemoteResource> findResource(final URI uri)
			throws URISyntaxException, IOException {
	}
}
