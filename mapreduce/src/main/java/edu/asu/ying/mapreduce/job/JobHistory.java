package edu.asu.ying.mapreduce.job;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public final class JobHistory implements Serializable {

  private final List<Entry> history = new LinkedList<>();

  public void visitedBy(String nodeName) {
    this.history.add(new Entry(nodeName));
  }

  public Entry getCurrent() {
    return ((LinkedList<Entry>) this.history).getLast();
  }

  public final class Entry implements Serializable {

    private static final long SerialVersionUID = 1L;

    private final String nodeName;
    private Action nodeAction;

    private Entry(String nodeName) {
      this.nodeName = nodeName;
    }

    public String getNodeID() {
      return this.nodeName;
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