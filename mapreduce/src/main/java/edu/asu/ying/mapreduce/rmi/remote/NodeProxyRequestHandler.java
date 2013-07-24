package edu.asu.ying.mapreduce.rmi.remote;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.common.event.EventHandler;
import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.rmi.Activator;


/**
 * {@code NodeProxyRequestHandler} receives {@link NodeProxyRequest} messages and returns a
 * {@link java.rmi.Remote} reference to an {@link Activator} instance.
 */
@Singleton
public final class NodeProxyRequestHandler
    implements EventHandler<Message> {

  // Receives incoming NodeProxyRequest messages.
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
  private NodeProxyRequestHandler(
      final MessageHandler incomingMessageHandler,
      final @SendMessageStream MessageOutputStream sendMessageStream,
      final Activator serverActivator) {

    this.serverActivator = serverActivator;
    this.sendMessageStream = sendMessageStream;
    this.onIncomingMessage = incomingMessageHandler.getIncomingMessageEvent();
    this.onIncomingMessage.attach(FilterClass.is(NodeProxyRequest.class), this);
  }


}
