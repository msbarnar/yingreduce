package edu.asu.ying.mapreduce.tasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.net.NodeIdentifier;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;

/**
 * {@code TaskHistory} keeps a journal of all of the schedulers that observe a particular task.
 * </p>
 * It is the scheduler's responsibility to append itself to the history.
 */
public final class TaskHistory implements Serializable {

  private static final long SerialVersionUID = 1L;

  /**
   * {@code SchedulerAction} represents something a scheduler might do with a task.
   */
  public static enum SchedulerAction {
    None,
    QueuedLocally,
    Forwarded,
    QueuedRemotely
  }

  /**
   * {@code NodeRole} represents the role that the current node plays in handling the task.
   */
  public static enum NodeRole {
    Responsible,    // The responsible node delegates the task
    Initial,        // The initial node contains the data for a particular task segment
    Child           // A child node was delegated a task by an initial node because the initial
                    // node's Local queue was full.
  }

  /**
   * {@code TaskHistory.Entry} is a single entry on the history journal.
   */
  public static final class Entry implements Serializable {

    private static final long SerialVersionUID = 1L;

    // Any node visiting the history should be able to contact this node by the URI.
    private NodeIdentifier nodeUri;
    // The role of the visiting node in carrying the task.
    private NodeRole nodeRole;
    // What the visiting scheduler ultimately did with the task.
    private SchedulerAction schedulerAction;

    public Entry() {
    }
    public Entry(final NodeIdentifier nodeUri) {
      this.nodeUri = nodeUri;
    }
    public Entry(final NodeIdentifier nodeUri, final NodeRole nodeRole,
                 final SchedulerAction schedulerAction) {

      this.nodeUri = nodeUri;
      this.nodeRole = nodeRole;
      this.schedulerAction = schedulerAction;
    }

    public final void setNodeUri(final NodeIdentifier uri) {
      this.nodeUri = uri;
    }
    public final NodeIdentifier getNodeUri() {
      return this.nodeUri;
    }


    public final void setSchedulerAction(final SchedulerAction action) {
      this.schedulerAction = action;
    }
    public final SchedulerAction getSchedulerAction() {
      return this.schedulerAction;
    }

    public NodeRole getNodeRole() {
      return nodeRole;
    }

    public void setNodeRole(final NodeRole nodeRole) {
      this.nodeRole = nodeRole;
    }
  }

  private final Deque<Entry> history = new ArrayDeque<>();

  public TaskHistory() {
  }

  public void append(final Entry entry) {
    this.history.push(entry);
  }

  /**
   * Returns the first (oldest) item in the history, or null if the history is empty.
   */
  public final @Nullable Entry first() {
    return this.history.peekLast();
  }

  /**
   * Returns the last (most recent) item in the history, or null if the history is empty.
   */
  public final @Nullable Entry last() {
    return this.history.peek();
  }

  /**
   * Returns the number of entries in the history.
   */
  public final int size() {
    return this.history.size();
  }

  /**
   * Returns {@code true} if the history contains 0 entries.
   */
  public final boolean isEmpty() {
    return this.history.isEmpty();
  }

  /**
   * Returns an immutable copy of the history.
   */
  public final ImmutableList<Entry> asList() {
    return ImmutableList.copyOf(this.history);
  }

  /**
   * Returns an iterator over an immutable copy of the history.
   * </p>
   * If you are going to use this more than once without modifying the collection, use
   * {@link #asList} to get a copy of the collection and iterate over that, instead.
   */
  public final Iterator<Entry> iterator() {
    return this.asList().iterator();
  }
}
