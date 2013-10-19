package edu.asu.ying.wellington.dfs.server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;

import edu.asu.ying.wellington.dfs.PageMetadata;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 * {@code PageTransfer} wraps a page's metadata along with a {@link RemoteInputStream} by which
 * that page can be downloaded. Sending a {@code PageTransfer} to remote node allows that node to
 * download the page from the local node.
 * <p/>
 * The {@code PageTransfer} also keeps track of which nodes have accepted the page so that
 * replication nodes can find each other.
 */
public final class PageTransfer implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final PageMetadata metadata;
  private final RemoteInputStream stream;
  // Keep track of the nodes responsible for this page
  private final Collection<RemoteNode> containerNodes;

  public PageTransfer(PageMetadata metadata, InputStream stream,
                      Collection<RemoteNode> containerNodes) {
    this.metadata = metadata;
    if (stream instanceof RemoteInputStream) {
      this.stream = (RemoteInputStream) stream;
    } else {
      this.stream = new SimpleRemoteInputStream(stream);
    }
    this.containerNodes = containerNodes;
  }

  public PageMetadata getMetadata() {
    return metadata;
  }

  public RemoteInputStream getStream() {
    return stream;
  }

  public Collection<RemoteNode> getContainerNodes() {
    return containerNodes;
  }
}
