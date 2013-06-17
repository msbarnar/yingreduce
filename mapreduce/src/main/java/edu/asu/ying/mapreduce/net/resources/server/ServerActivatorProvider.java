package edu.asu.ying.mapreduce.net.resources.server;

import com.google.inject.Inject;
import edu.asu.ying.mapreduce.common.events.EventHandler;
import edu.asu.ying.mapreduce.common.events.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.messaging.ExceptionMessage;
import edu.asu.ying.mapreduce.messaging.FilterMessage;
import edu.asu.ying.mapreduce.messaging.IncomingMessageEvent;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.net.resources.ResourceRequest;
import edu.asu.ying.mapreduce.net.resources.ResourceResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URISyntaxException;


/**
 * {@code ServerActivatorProvider} receives {@link ResourceRequest} messages with the destination URI scheme
 * "{@code activator}" and returns a {@link Remote} reference to an {@link Activator}.
 */
public final class ServerActivatorProvider
	implements EventHandler<Message>
{
	private final static String ACTIVATOR_SCHEME = "activator";

	// Receives incoming ResourceRequest messages with the URI scheme "activator".
	private final FilteredValueEvent<Message> onIncomingMessage;
	// Sends our responses
	private final MessageOutputStream sendMessageStream;

	/**
	 * Binds the provider to the {@link edu.asu.ying.mapreduce.messaging.IncomingMessageEvent} with an appropriate
	 * filter for receiving {@link Activator} requests.
	 */
	@Inject
	private ServerActivatorProvider(final @IncomingMessageEvent FilteredValueEvent<Message> onIncomingMessage,
	                                final @SendMessageStream MessageOutputStream sendMessageStream) {
		this.sendMessageStream = sendMessageStream;
		this.onIncomingMessage = onIncomingMessage;
		this.onIncomingMessage.attach(Filter.on.allOf(
										Filter.on.classIs(ResourceRequest.class),
		                                FilterMessage.on.destinationUri.scheme(ACTIVATOR_SCHEME))
										, this);
	}

	/**
	 * Receives a {@link ResourceRequest} message from the {@link IncomingMessageEvent}.
	 * @return true, so that it always remains bound to the {@code IncomingMessageEvent}.
	 */
	@Override
	public boolean onEvent(final @Nonnull Object sender, final @Nullable Message message) {
		if (message == null) {
			return true;
		}
		final Message response = this.processRequest(message);
		this.sendMessageStream.write(response);

		return true;
	}

	private final Message processRequest(final Message request) {
		try {
			final ResourceResponse response = ResourceResponse.inResponseTo(request);
		} catch (final URISyntaxException e) {
			return new ExceptionMessage("Exception processing URI of resource request.", e);
		}
	}
}
