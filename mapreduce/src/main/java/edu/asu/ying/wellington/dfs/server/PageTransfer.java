package edu.asu.ying.wellington.dfs.server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

import java.io.InputStream;
import java.io.Serializable;
import java.util.UUID;

import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 * {@code PageTransfer} wraps a page's metadata along with a {@link RemoteInputStream} by which
 * that page can be downloaded. Sending a {@code PageTransfer} to remote node allows that node to
 * download the page from the local node.
 * <p/>
 * The {@code PageTransfer} also keeps track of which nodes have accepted the page so that
 * replication nodes can find each other.
 */
public final class PageTransfer implements HasPageMetadata, Serializable {

  private static final long SerialVersionUID = 1L;

  private final String id;
  private final RemoteNode sendingNode;
  private final int replication;
  private final PageMetadata metadata;
  private final RemoteInputStream stream;

  public PageTransfer(RemoteNode sendingNode, int replication,
                      PageMetadata metadata, InputStream stream) {

    this.id = UUID.randomUUID().toString();
    this.sendingNode = sendingNode;
    this.replication = replication;
    this.metadata = metadata;
    this.stream = new SimpleRemoteInputStream(stream);
  }

  public String getId() {
    return id;
  }

  public RemoteNode getSendingNode() {
    return sendingNode;
  }

  public PageMetadata getMetadata() {
    return metadata;
  }

  public RemoteInputStream getInputStream() {
    return stream;
  }
}
