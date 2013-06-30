package edu.asu.ying.mapreduce.net.messaging.activator;

import com.google.inject.Inject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.common.event.EventHandler;
import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.net.messaging.ExceptionMessage;
import edu.asu.ying.mapreduce.net.messaging.IncomingMessageEvent;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.rmi.Activator;


/**
 * {@code ActivatorRequestHandler} receives {@link ActivatorRequest} messages and returns a
 * {@link java.rmi.Remote} reference to an {@link Activator} instance.
 */
public final class ActivatorRequestHandler
    implements EventHandler<Message> {

  // Receives incoming ActivatorRequest messages.
  private final FilteredValueEvent<Message> onIncomingMessage;
  // Sends our responses
  private final MessageOutputStream sendMessageStream;
  // The Activator instance we export to client nodes
  private final Activator serverActivator;

  /**
   * Binds the provider to the {@link ActivatorMessageEvent}
   * with an appropriate filter for receiving {@link Activator} requests.
   */
  @Inject
  private ActivatorRequestHandler(
      final @ActivatorMessageEvent FilteredValueEvent<Message> onIncomingMessage,
      final @SendMessageStream MessageOutputStream sendMessageStream,
      final Activator serverActivator) {

    this.serverActivator = serverActivator;
    this.sendMessageStream = sendMessageStream;
    this.onIncomingMessage = onIncomingMessage;
    this.onIncomingMessage.attach(FilterClass.is(ActivatorRequest.class), this);
  }

  /**
   * Receives a {@link ActivatorRequest} message from the {@link IncomingMessageEvent}.
   *
   * @return true, so that it always remains bound to the {@code IncomingMessageEvent}.
   */
  @Override
  public boolean onEvent(final @Nonnull Object sender, final @Nullable Message request) {
    if (request == null) {
      return true;
    }
    Message response = this.processRequest((ActivatorRequest) request);
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
   * Exports a {@link java.rmi.Remote} instance of an {@link Activator} proxy and returns it in an
   * {@link ActivatorResponse}.
   *
   * @param request the request for an {@link Activator} proxy.
   * @return a {@link ActivatorResponse} wrapping an {@link Activator} proxy.
   */
  private final ActivatorResponse processRequest(final ActivatorRequest request) {
    final ActivatorResponse response = ActivatorResponse.inResponseTo(request);

    final Activator instance;
    try {
      // Export the RMI Remote proxy and return it in the message
      instance = (Activator) UnicastRemoteObject.exportObject(this.serverActivator,
                                                              8000+(new Random()).nextInt(2000));
      response.setInstance(instance);

    } catch (final RemoteException e) {
      response.setException(e);
    }

    return response;
  }
}
