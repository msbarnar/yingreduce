package edu.asu.ying.database.page;

import edu.asu.ying.common.sink.RemoteSink;

/**
 * {@code RemotePageSink} is necessary for binding the RMI interface because generics are
 * second-class citizens.
 */
public interface RemotePageSink extends RemoteSink<Page> {

}
