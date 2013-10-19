package edu.asu.ying.wellington.dfs.server;

import edu.asu.ying.common.event.Event;
import edu.asu.ying.common.event.EventHandler;
import edu.asu.ying.wellington.dfs.PageMetadata;

/**
 *
 */
public interface PageInEvent extends Event<EventHandler<PageMetadata>, PageMetadata> {

}
