package edu.asu.ying.mapreduce.rmi.resource;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import edu.asu.ying.mapreduce.messaging.*;
import edu.asu.ying.mapreduce.messaging.io.MessageOutputStream;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;


/**
 * The {@link SyncResourceFinder} locates {@link RemoteResource} objects on a Kademlia network and returns
 * their references.
 * <p>
 * The types of resources located, organized by the {@link ResourceIdentifier} {@code scheme} part are:
 * <ul>
 *     <li>{@code activator}: a {@link java.rmi.Remote} object activator that returns remote object references.</li>
 * </ul>
 */
public final class SyncResourceFinder
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
	private SyncResourceFinder(final @SendMessageStream MessageOutputStream sendStream,
	                          final @Named("resource") MessageDispatch responseDispatch) {

		this.messageOutput = sendStream;
		this.responseDispatch = responseDispatch;
	}

	/**
	 * Constructs a {@link edu.asu.ying.mapreduce.rmi.resource.GetResourceMessage} with the resource identifier and node key in the URI.
	 * @param uri the identifier used to locate the resource.
	 * @return a future response to be fulfilled by the {@link MessageDispatch} when it receives a response.
	 * @throws IOException if the underlying network implementation throws an exception or no response was received
	 * from the remote host.
	 */
	@Override
	public final List<RemoteResource> findResource(final ResourceIdentifier uri)
			throws URISyntaxException, IOException {
		// Build the message from the URI
		final Message message = new GetResourceMessage(uri);
		// Register to get a response from the message dispatch matching the request by ID
		final FutureMessage response = this.responseDispatch.getFutureMessage();
		response.filter.allOf.id(message.getId()).type(GetResourceResponse.class);
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
