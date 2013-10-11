package edu.asu.ying.database.net;

import edu.asu.ying.common.event.RemoteSink;
import edu.asu.ying.database.page.Page;

/**
 * {@code RemotePageSink} is necessary for binding the RMI interface because generics are
 * second-class citizens.
 */
public interface RemotePageSink extends RemoteSink<Page> {

}
