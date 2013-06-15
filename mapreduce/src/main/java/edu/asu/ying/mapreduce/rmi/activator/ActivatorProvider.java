package edu.asu.ying.mapreduce.rmi.activator;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import edu.asu.ying.mapreduce.messaging.*;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.rmi.resource.GetResourceResponse;
import edu.asu.ying.mapreduce.rmi.resource.ResourceProvider;
import edu.asu.ying.mapreduce.rmi.resource.GetResourceMessage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * The {@link ActivatorProvider} intercepts {@link edu.asu.ying.mapreduce.rmi.resource.GetResourceMessage} and
 * interprets requests for {@link Activator} references.
 */
public final class ActivatorProvider
		implements ResourceProvider, FutureCallback<Message>
{
	// Get our messages from here
	private final MessageDispatch dispatch;
	// Write messages here to send them
	private final MessageOutputStream sendStream;
	// Produces activator instances to provide
	private final Activator activator;

	@Inject
	private ActivatorProvider(final @Named("resource") MessageDispatch dispatch,
	                         final @SendMessageStream MessageOutputStream sendStream,
	                         final Activator activator) {
		this.dispatch = dispatch;
		this.sendStream = sendStream;
		this.activator = activator;
		this.bind();
	}

	/**
	 * Register with the dispatch to receive {@link edu.asu.ying.mapreduce.rmi.resource.GetResourceMessage}.
	 */
	private final void bind() {
		// Get a message in the future that is of the class GetResourceMessage and has the typename "activator"
		final FutureMessage message = this.dispatch.getFutureMessage();
		message.filter.allOf.type(GetResourceMessage.class).property("resource-typename", "activator");
		// Register a callback from the message when it arrives
		message.addCallback(this);
	}

	/**
	 * Called when a message arrives from the {@link MessageDispatch}.
	 */
	@Override
	public void onSuccess(final Message message) {
		// FutureMessage is a one-time deal; make sure we put another in the dispatch queue
		this.bind();

		final GetResourceResponse response = new GetResourceResponse(message);

		try {
			// Get an proxy to allow accession of the Activator object.
			final Activator activatorProxy
					= (Activator) UnicastRemoteObject.exportObject(this.activator, 3333);
			response.setResource(activatorProxy);
		} catch (final RemoteException e) {
			response.setException(e);
		}

		try {
			this.sendStream.write(response);
		} catch (final IOException e) {
			// TODO: logging
			e.printStackTrace();
		}
	}

	/**
	 * Called when there was a problem getting the message from the {@link MessageDispatch}.
	 */
	@Override
	public void onFailure(final Throwable throwable) {
		// FutureMessage is a one-time deal; make sure we put another in the dispatch queue
		this.bind();

		// TODO: Logging
		throwable.printStackTrace();
	}
}
