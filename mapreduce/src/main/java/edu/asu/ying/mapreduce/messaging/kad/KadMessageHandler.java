package edu.asu.ying.mapreduce.messaging.kad;

import com.google.inject.Inject;
import edu.asu.ying.mapreduce.messaging.*;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.MessageHandler;
import il.technion.ewolf.kbr.Node;

import java.io.IOException;
import java.io.Serializable;


/**
 * {@link KadMessageHandler} chains messages received by a {@link il.technion.ewolf.kbr.KeybasedRouting} object
 * to a {@link edu.asu.ying.mapreduce.messaging.MessageOutputStream}.
 */
public final class KadMessageHandler
	implements MessageHandler
{
	// The node we receive messages from
	private final KeybasedRouting kadNode;
	// The stream they are written to
	private final MessageOutputStream messageStream;


	@Inject
	public KadMessageHandler(final KeybasedRouting inputNode, final MessageOutputStream messageStream) {
		this.kadNode = inputNode;
		this.messageStream = messageStream;

		// TODO: un-hardcore tags
		this.kadNode.register("mapreduce", this);
	}

	/**
	 * Relays an incoming message to the underlying {@link MessageOutputStream}.
	 * @param from the {@link Node} sending the message
	 * @param tag the arrived message tag (always "mapreduce")
	 * @param content the sent object
	 */
	@Override
	public void onIncomingMessage(final Node from, final String tag, final Serializable content) {
		try {
			if (!(content instanceof Message)) {
				throw new InvalidContentException();
			}
			this.messageStream.write((Message) content);
		} catch (final IOException e) {
			// TODO: logging
			e.printStackTrace();
		}
	}

	/**
	 * Relays an incoming message to the underlying {@link MessageOutputStream} and returns an
	 * {@link AcknowledgementMessage} to the sender.
	 * <p>
	 * If {@code content} is not a valid {@link Message}, returns an {@link InvalidContentException} to the sender.
	 * <p>
	 * If an exception is thrown writing the message to the stream, the exception is returned to the sender.
	 * @param from the {@link Node} sending the message
	 * @param tag the arrived message tag (always "mapreduce")
	 * @param content the sent object
	 * @return {@link AcknowledgementMessage} or {@link ExceptionMessage} signaling the reception success.
	 */
	@Override
	public Serializable onIncomingRequest(final Node from, final String tag, final Serializable content) {
		if (!(content instanceof Message)) {
			return new ExceptionMessage(new InvalidContentException());
		}
		try {
			this.messageStream.write((Message) content);
		} catch (final IOException e) {
			return new ExceptionMessage(e);
		}
		return new AcknowledgementMessage();
	}
}
