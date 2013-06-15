package edu.asu.ying.mapreduce.messaging;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * {@code FutureMessageHandler} waits for {@link Message} callbacks from the underlying {@link FilteredMessageHandler} and
 * fulfills a waiting {@link ListenableFuture} when a message arrives.
 */
public final class FutureMessageHandler
{
}
