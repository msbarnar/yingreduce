package edu.asu.ying.mapreduce.rmi.finder.kad;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import edu.asu.ying.mapreduce.messaging.*;
import edu.asu.ying.mapreduce.net.RemoteResource;
import edu.asu.ying.mapreduce.rmi.finder.ResourceFinder;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
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
	private final MessageSink sendSink;

	/**
	 * Initializes the resource finder with an outgoing message sink to relay its messages.
	 * @param sendSink the message sink chain that will relay the resource finder's messages to their destination.
	 */
	@Inject
	public KadResourceFinder(final @SendMessageSink MessageSink sendSink) {
		this.sendSink = sendSink;
	}

	/**
	 * Constructs a {@link GetResourceMessage} with the resource identifier and node key in the URI.
	 * @param uri the identifier used to locate the resource.
	 * @return one or more references to resources matching the URI.
	 * @throws URISyntaxException if the URI is not a valid {@link RemoteResource} identifier.
	 * @throws IOException if the underlying network implementation throws an exception.
	 */
	@Override
	public final List<RemoteResource> findResource(final URI uri)
			throws URISyntaxException, IOException {
		// Build the message from the URI
		final Message message = new GetResourceMessage(uri);
		// Pass the message to the network and block until we get a response
		// TODO: async
		final Message response = this.sendSink.accept(message);

		// Check for an exceptional response
		if (response instanceof ExceptionMessage) {
			throw ((ExceptionMessage) response).getException();
		}

		if (!(response instanceof GetResourceMessage)) {
			throw new UnexpectedMessageException(response, GetResourceMessage.class);
		}

		final Optional<Serializable> resources =
				Optional.fromNullable(((GetResourceMessage) response).getProperties().get("resources"));

		// No resources found or property of the wrong type
		if (!resources.isPresent() || !(resources.get() instanceof List)) {
			return null;
		}

		return (List<RemoteResource>) resources.get();
	}
}
