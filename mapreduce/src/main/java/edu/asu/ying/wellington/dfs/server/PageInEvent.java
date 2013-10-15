package edu.asu.ying.wellington.dfs.server;

import edu.asu.ying.common.event.Event;
import edu.asu.ying.common.event.EventHandler;
import edu.asu.ying.wellington.dfs.Page;

/**
 *
 */
public interface PageInEvent extends Event<EventHandler<Page>, Page> {

}
