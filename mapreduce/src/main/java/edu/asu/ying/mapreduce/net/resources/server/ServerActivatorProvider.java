package edu.asu.ying.mapreduce.net.resources.server;

import com.google.inject.Inject;
import com.google.inject.Provider;
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
import edu.asu.ying.mapreduce.rmi.activator.Activator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * {@code ServerActivatorProvider} receives {@link ResourceRequest} messages with the destination URI scheme
 * "{@code activator}" and returns a {@link java.rmi.Remote} reference to an {@link Activator}.
 */
public final class ServerActivatorProvider
	implements ServerResourceProvider, EventHandler<Message>
{
	private final static String ACTIVATOR_SCHEME = "activator";

	// Receives incoming ResourceRequest messages with the URI scheme "activator".
	private final FilteredValueEvent<Message> onIncomingMessage;
	// Sends our responses
	private final MessageOutputStream sendMessageStream;
	// Provides the Activator instance we export to client nodes
	private final Provider<Activator> activatorProvider;

	/**
	 * Binds the provider to the {@link edu.asu.ying.mapreduce.messaging.IncomingMessageEvent} with an appropriate
	 * filter for receiving {@link Activator} requests.
	 */
	@Inject
	private ServerActivatorProvider(final @IncomingMessageEvent FilteredValueEvent<Message> onIncomingMessage,
	                                final @SendMessageStream MessageOutputStream sendMessageStream,
	                                final Provider<Activator> activatorProvider) {

		this.activatorProvider = activatorProvider;
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
	public boolean onEvent(final @Nonnull Object sender, final @Nullable Message request) {
		if (request == null) {
			return true;
		}
		Message response = this.processRequest(request);
		try {
			this.sendMessageStream.write(response);
		} catch (final IOException e) {
			// TODO: logging
			e.printStackTrace();
			try {
				response = new ExceptionMessage("Exception sending response", e);
				this.sendMessageStream.write(response.makeResponseTo(request));
			} catch (final IOException e2) {
				// TODO: logging
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * Exports a {@link java.rmi.Remote} instance of an {@link Activator} proxy and returns it in a
	 * {@link ResourceResponse}.
	 * @param request the request for an {@link Activator} proxy.
	 * @return a {@link ResourceResponse} wrapping an {@link Activator} proxy.
	 */
	private final Message processRequest(final Message request) {
		final ResourceResponse response;
		try {
			response = ResourceResponse.inResponseTo(request);
		} catch (final URISyntaxException e) {
			// Can't make a valid response message
			return new ExceptionMessage("Exception processing URI of resource request.", e).makeResponseTo(request);
		}

		final Activator instance;
		try {
			// Export the RMI Remote proxy and return it in the message
			instance = (Activator) UnicastRemoteObject.exportObject(this.activatorProvider.get());
			response.setResourceInstance(instance);
		} catch (final RemoteException e) {
			response.setException(e);
		}

		return response;
	}
}
