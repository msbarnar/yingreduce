package edu.asu.ying.mapreduce.node.io;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.asu.ying.mapreduce.node.io.message.Message;

/**
 * A {@code Channel} provides a single point of access for input from and output to the underlying
 * network.
 */
public interface Channel {

  void registerMessageHandler(final MessageHandler handler, final String tag);

  MessageOutputStream getMessageOutputStream();
}
