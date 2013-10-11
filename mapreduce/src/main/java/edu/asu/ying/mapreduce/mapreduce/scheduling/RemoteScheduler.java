package edu.asu.ying.mapreduce.mapreduce.scheduling;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskCompletion;
import edu.asu.ying.mapreduce.mapreduce.task.TaskSchedulingResult;
import edu.asu.ying.p2p.rmi.Activatable;

/**
 * {@code RemoteScheduler} provides the interface for remote peers to access the scheduler on the
 * local node. Remote peers use this interface to delegate tasks to the local node.
 */
public interface RemoteScheduler extends Activatable {

  /**
   * As an instruction to a {@link Job}'s {@code responsible node}, {@code startJob} begins the
   * process at that node of delegating the job into tasks.
   */
  JobSchedulingResult acceptJobAsResponsibleNode(final Job job) throws RemoteException;

  /**
   * At a task's {@code initial node} (the node which contains the data pages referenced by the
   * task), {@code delegateTask} begins scheduling the task for local execution. </p> At a node that
   * is <i>not</i> the task's {@code initial node}, {@code delegateTask} schedules the task as a
   * {@code remote} task or forwards it to an available peer.
   */
  TaskSchedulingResult acceptTask(final Task task) throws RemoteException;

  TaskSchedulingResult acceptInitialTask(final Task task) throws RemoteException;

  void reduceTaskCompletion(final TaskCompletion completion) throws RemoteException;

  /**
   * Returns an integer indicating the absolute quantity of load on this node. A higher value means
   * a more heavily loaded node (and less desirable forwarding target).
   */
  int getBackpressure() throws RemoteException;
}
