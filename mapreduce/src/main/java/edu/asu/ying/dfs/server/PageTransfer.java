package edu.asu.ying.dfs.server;

import com.healthmarketscience.rmiio.RemoteInputStream;

import java.io.Serializable;
import java.util.UUID;

import edu.asu.ying.dfs.Page;

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

  public final String id;
  public final RemoteNode sendingNode;
  public final int replication;
  public final Page page;

  public PageTransfer(RemoteNode sendingNode, int replication, Page page) {
    this.id = UUID.randomUUID().toString();
    this.sendingNode = sendingNode;
    this.replication = replication;
    this.page = page;
  }
}
