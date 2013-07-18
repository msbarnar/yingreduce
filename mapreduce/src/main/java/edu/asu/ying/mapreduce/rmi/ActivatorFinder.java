package edu.asu.ying.mapreduce.rmi;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.inject.Inject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executors;

import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.common.concurrency.FilteredFutures;
import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.common.filter.FilterString;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.messaging.FilterMessage;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.messaging.activator.ActivatorMessageEvent;
import edu.asu.ying.mapreduce.net.messaging.activator.ActivatorRequest;


public final class ActivatorFinder
    implements FutureCallback<Message> {

  /**
   * ***************************************************************** Getting resource
   */

  // Used to send messages on the network
  private final MessageOutputStream sendStream;
  private final FilteredValueEvent<Message> onIncomingMessage;

  // Spawns our listening threads that wait for incoming messages.
  private final ListeningExecutorService executor
      = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

  // We technically can synchronize on the non-final unfulfilledResources deque, but we technically
  // can do a lot of things that we shouldn't.
  private final Object resourcesLock = new Object();
  private Deque<SettableFuture<Activator>> unfulfilledActivators;

  @Inject
  private ActivatorFinder(@SendMessageStream MessageOutputStream sendStream,
                          MessageHandler incomingMessageHandler)
  {

    this.sendStream = sendStream;
    this.onIncomingMessage = incomingMessageHandler.getIncomingMessageEvent();
  }

  /**
   * Gets {@code k} promises of a single resource from {@code k} different nodes.
   *
   * @param uri  the address of the node(s) whose resource to get. The number of nodes contacted is
   *             specified by the {@code replication} part of the URI.
   * @param args properties to supply the resource provider.
   * @return a number of promises not greater than the value of {@code replication} in the URI
   *         (default 1).
   */
  public final List<ListenableFuture<Activator>> getFutureActivators(final NodeURI uri,
                                                            final Properties args)
      throws URISyntaxException, IOException {
    // Set up the request
    final Message request = this.createRequest(uri, args);

    // Get future responses to our request
    final Deque<ListenableFuture<Message>> responses
        = new ArrayDeque<>(FilteredFutures.getFrom(this.onIncomingMessage)
                               .get(request.getReplication())
                               .filter(
                                   Filter.on.allOf(
                                       FilterClass.is(ResourceResponse.class),
                                       FilterMessage.id(FilterString.equalTo(request.getId()))
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
    // unfulfilled futures before we return them, so block the other thread from popping off the
    // deque until we've finished returning a copy of it.
    synchronized (this.resourcesLock) {
      // Send the request
      final int messagesSent = this.sendStream.write(request);
      // Trim the expected responses in case some of the messages failed to send

      for (int i = 0; i < (responses.size() - messagesSent); i++) {
        final ListenableFuture<Message> last = responses.peekLast();
        if (last != null && last.cancel(false)) {
          responses.removeLast();
          this.unfulfilledResources.removeLast();
        }
      }

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
    final Throwable exception = response.getException();
    if (exception != null) {
      this.onFailure(exception);
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
        this.unfulfilledResources.pop().setException(throwable);
      }
    }
  }

  /**
   * Creates a {@link edu.asu.ying.mapreduce.net.messaging.activator.ActivatorRequest} for the
   * resource at the given URI.
   */
  private Message createRequest(final NodeURI uri, final Properties args)
      throws URISyntaxException {

    final ActivatorRequest request = new ActivatorRequest(uri);
    request.setArguments(args);
    return request;
  }
}
