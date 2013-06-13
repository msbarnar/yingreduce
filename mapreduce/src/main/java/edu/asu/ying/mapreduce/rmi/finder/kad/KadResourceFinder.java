package edu.asu.ying.mapreduce.rmi.finder.kad;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import edu.asu.ying.mapreduce.messaging.*;
import edu.asu.ying.mapreduce.net.RemoteResource;
import edu.asu.ying.mapreduce.rmi.finder.ResourceFinder;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;


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
	// We pass messages here to send them
	private final MessageOutputStream messageOutput;
	// Provides Future<> results for messages for which we expect responses.
	private final MessageDispatch responseDispatch;

	/**
	 * Initializes the resource finder with an outgoing message stream to contact remote hosts.
	 * @param sendStream the message output stream that will convey messages to remote hosts.
	 */
	@Inject
	public KadResourceFinder(final @SendMessageStream MessageOutputStream sendStream,
	                         final MessageDispatch responseDispatch) {

		this.messageOutput = sendStream;
		this.responseDispatch = responseDispatch;
	}

	/**
	 * Constructs a {@link GetResourceMessage} with the resource identifier and node key in the URI.
	 * @param uri the identifier used to locate the resource.
	 * @return a future response to be fulfilled by the {@link MessageDispatch} when it receives a response.
	 * @throws URISyntaxException if the URI is not a valid {@link RemoteResource} identifier.
	 * @throws IOException if the underlying network implementation throws an exception or no response was received
	 * from the remote host.
	 */
	@Override
	public final List<RemoteResource> findResource(final URI uri)
			throws URISyntaxException, IOException {
		// Build the message from the URI
		final Message message = new GetResourceMessage(uri);
		// Register to get a response from the message dispatch
		final ListenableFuture<Message> response = this.responseDispatch.getFutureMessageById(message.getId());
		// Write the message to the network
		this.messageOutput.write(message);

		// Wait for a response; timeout after 20 seconds
		final Message responseMessage;
		try {
			responseMessage = response.get(20, TimeUnit.SECONDS);
		} catch (final InterruptedException | ExecutionException | TimeoutException e) {
			throw new IOException(e);
		}

		// Check for an exceptional response
		if (responseMessage instanceof ExceptionMessage) {
			throw ((ExceptionMessage) responseMessage).getException();
		}

		if (!(responseMessage instanceof GetResourceMessage)) {
			throw new UnexpectedMessageException(responseMessage, GetResourceMessage.class);
		}

		final Optional<Serializable> resources =
				Optional.fromNullable(((GetResourceMessage) responseMessage).getProperties().get("resources"));

		// No resources found or property of the wrong type
		if (!resources.isPresent() || !(resources.get() instanceof List)) {
			return null;
		}

		return (List<RemoteResource>) resources.get();
	}
}
