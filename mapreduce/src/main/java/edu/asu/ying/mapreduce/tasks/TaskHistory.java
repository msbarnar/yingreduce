package edu.asu.ying.mapreduce.tasks;

import java.io.Serializable;
import java.util.ArrayDeque;

import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;

/**
 * {@code TaskHistory} keeps a journal of all of the schedulers that observe a particular task.
 * </p>
 * It is the scheduler's responsibility to append itself to the history.
 */
public final class TaskHistory extends ArrayDeque<TaskHistory.Entry> {

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
   * {@code TaskHistory.Entry} is a single entry on the history journal.
   */
  public static final class Entry implements Serializable {

    private static final long SerialVersionUID = 1L;

    // Any node visiting the history should be able to contact this node by the URI.
    private ResourceIdentifier nodeUri;
    // Universally unique identifier representing the scheduler visiting the history.
    private String schedulerId;
    // What the visiting scheduler ultimately did with the task.
    private SchedulerAction schedulerAction;

    public Entry() {
    }
    public Entry(final ResourceIdentifier nodeUri, final String schedulerId,
                 final SchedulerAction schedulerAction) {
      this.nodeUri = nodeUri;
      this.schedulerId = schedulerId;
      this.schedulerAction = schedulerAction;
    }

    public final void setNodeUri(final ResourceIdentifier uri) {
      this.nodeUri = uri;
    }
    public final ResourceIdentifier getNodeUri() {
      return this.nodeUri;
    }

    public final void setSchedulerId(final String id) {
      this.schedulerId = id;
    }
    public final String getSchedulerId() {
      return this.schedulerId;
    }

    public final void setSchedulerAction(final SchedulerAction action) {
      this.schedulerAction = action;
    }
    public final SchedulerAction getSchedulerAction() {
      return this.schedulerAction;
    }
  }

  public TaskHistory() {
  }
}
