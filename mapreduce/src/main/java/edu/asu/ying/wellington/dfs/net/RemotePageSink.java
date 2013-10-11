package edu.asu.ying.wellington.dfs.net;

import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.wellington.dfs.page.Page;

/**
 * {@code RemotePageSink} is necessary for binding the RMI interface because generics are
 * second-class citizens.
 */
public interface RemotePageSink extends RemoteSink<Page> {

}
