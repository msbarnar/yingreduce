package edu.asu.ying.mapreduce.net.resource;

import com.google.common.util.concurrent.*;
import com.google.inject.Inject;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.common.concurrency.FilteredFutures;
import edu.asu.ying.mapreduce.common.events.FilteredValueEvent;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.messaging.IncomingMessageEvent;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.messaging.FilterMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * {@code RemoteResourceFinder} facilitates asynchronous getting of {@link RemoteResource} objects from remote nodes.
 * </p>
 * The class is parameterized on the type of resource that it gets.
 */
public final class RemoteResourceFinder<V extends RemoteResource>
	implements ClientResourceProvider, FutureCallback<Message>
{
	/********************************************************************
	 * Getting resources
	 */

	// Used to send messages on the network
	private final MessageOutputStream sendStream;
	private final FilteredValueEvent<Message> onIncomingMessage;

	// Spawns our listening threads that wait for incoming messages.
	private final ListeningExecutorService executor
			= MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

	// We technically can synchronize on the non-final unfulfilledResources deque, but we technically can do a lot
	// of things that we shouldn't.
	private final Object resourcesLock = new Object();
	private Deque<SettableFuture<V>> unfulfilledResources;

	@Inject
	private RemoteResourceFinder(@SendMessageStream MessageOutputStream sendStream,
	                             @IncomingMessageEvent FilteredValueEvent<Message> onIncomingMessage) {

		this.sendStream = sendStream;
		this.onIncomingMessage = onIncomingMessage;
	}

	/**
	 * Gets {@code k} promises of a single resource from {@code k} different nodes.
	 * @param uri the address of the node(s) whose resource to get. The number of nodes contacted is specified by the
	 *            {@code replication} part of the URI.
	 * @param args properties to supply the resource provider.
	 * @return a number of promises not greater than the value of {@code replication} in the URI (default 1).
	 */
	@Override
	public final List<ListenableFuture<V>> getFutureResources(final ResourceIdentifier uri, final Properties args)
			throws URISyntaxException, IOException {
		// Set up the request
		final Message request = this.createRequest(uri, args);

		// Get future responses to our request
		final Deque<ListenableFuture<Message>> responses
				= new ArrayDeque<>(FilteredFutures.getFrom(this.onIncomingMessage)
					                 .get(request.getReplication())
									 .filter(
											 Filter.on.allOf(
													 Filter.on.classIs(ResourceResponse.class),
													 FilterMessage.on.id(request.getId())
											 )
									 ));

		// Set up the return value
		this.unfulfilledResources = new ArrayDeque<>(responses.size());
		// Listen for the response futures being set
		for (final ListenableFuture<Message> response : responses) {
			Futures.addCallback(response, this, this.executor);
			this.unfulfilledResources.push(SettableFuture.<V>create());
		}

		// We're currently getting messages from the event thread, but we have some work to do on the
		// unfulfilled futures before we return them, so block the other thread from popping off the deque until
		// we've finished returning a copy of it.
		synchronized (this.resourcesLock) {
			// Send the request
			final int messagesSent = this.sendStream.write(request);
			// Trim the expected responses in case some of the messages failed to send

			for (int i = 0; i < (responses.size() - messagesSent); i++) {
				if (responses.peekLast().cancel(false)) {
					responses.removeLast();
					this.unfulfilledResources.removeLast();
				}
			}

			// Similarly, don't let the other thread pop from this deque while we're iterating it for a copy
			return new ArrayList<ListenableFuture<V>>(this.unfulfilledResources);
		}
	}

	/**
	 * Callback for message arrival; fulfills one of the unfulfilled resources
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onSuccess(final Message message) {
		final ResourceResponse response = (ResourceResponse) message;
		// Responses can return exceptions, so fail on that
		if (response.getException() != null) {
			this.onFailure(response.getException());
		} else {
			// Avoid popping off this deque while anyone else is iterating it
			synchronized (this.resourcesLock) {
				if (this.unfulfilledResources.peek() != null) {
					// Supress warning: V is constrained on RemoteResource
					this.unfulfilledResources.pop().set((V) response.getResourceInstance());
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onFailure(final Throwable throwable) {
		// Set a resource exception instead of the resource they wanted
		// Avoid popping off this deque while anyone else is iterating it
		synchronized (this.resourcesLock) {
			if (this.unfulfilledResources.peek() != null) {
				// Supress warning: V is constrained on RemoteResource
				this.unfulfilledResources.pop().set((V) new ResourceException(throwable));
			}
		}
	}

	/**
	 * Creates a {@link ResourceRequest} for the resource at the given URI.
	 */
	private Message createRequest(final ResourceIdentifier uri, final Properties args)
			throws URISyntaxException {

		final ResourceRequest request = ResourceRequest.locatedBy(uri);
		request.setArguments(args);
		return request;
	}
}
