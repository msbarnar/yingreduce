package edu.asu.ying.mapreduce.mapreduce.job;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.asu.ying.mapreduce.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.mapreduce.net.NodeIdentifier;

/**
 *
 */
public final class JobHistory implements Serializable {

  private final List<Entry> history = new LinkedList<>();

  public void touch(LocalNode node) {
    this.history.add(new Entry(node));
  }

  public Entry getCurrent() {
    return ((LinkedList<Entry>) this.history).getLast();
  }

  public final class Entry implements Serializable {

    private static final long SerialVersionUID = 1L;

    private final NodeIdentifier nodeID;
    private Action nodeAction;

    private Entry(LocalNode node) {
      this.nodeID = node.getNodeID();
    }

    public NodeIdentifier getNodeID() {
      return this.nodeID;
    }

    public void setAction(Action action) {
      this.nodeAction = action;
    }
  }

  public enum Action {
    AcceptedResponsibility,
    ForwardedToResponsibleNode,
    ForwardFailed
  }
}