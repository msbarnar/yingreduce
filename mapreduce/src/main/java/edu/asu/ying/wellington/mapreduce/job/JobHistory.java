package edu.asu.ying.wellington.mapreduce.job;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.asu.ying.wellington.mapreduce.server.NodeIdentifier;

/**
 *
 */
public final class JobHistory implements Serializable {

  private final List<Entry> history = new LinkedList<>();

  public void visitedBy(NodeIdentifier nodeID) {
    this.history.add(new Entry(nodeID));
  }

  public Entry getCurrent() {
    return ((LinkedList<Entry>) this.history).getLast();
  }

  public final class Entry implements Serializable {

    private static final long SerialVersionUID = 1L;

    private final NodeIdentifier nodeID;
    private Action nodeAction;

    private Entry(NodeIdentifier nodeID) {
      this.nodeID = nodeID;
    }

    public NodeIdentifier getNodeID() {
      return this.nodeID;
    }

    public void setAction(Action action) {
      this.nodeAction = action;
    }
  }

  public enum Action {
    AcceptedJob,
    RejectedJob,
    ForwardedToResponsibleNode,
    ForwardFailed
  }
}