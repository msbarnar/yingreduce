package edu.asu.ying.mapreduce.node.messaging;

import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;

/**
 *
 */
public interface MessageHandler {

  MessageEvent getIncomingMessageEvent();
}
