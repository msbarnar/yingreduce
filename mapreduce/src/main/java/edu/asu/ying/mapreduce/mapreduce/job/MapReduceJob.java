package edu.asu.ying.mapreduce.mapreduce.job;

import edu.asu.ying.mapreduce.common.HasProperties;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.mapreduce.task.TaskID;
import edu.asu.ying.mapreduce.yingtable.TableID;
import edu.asu.ying.p2p.RemoteNode;

/**
 *
 */
public class MapReduceJob implements Job {

  private static final long SerialVersionUID = 1L;

  protected static final class Property {
    static final String JobID = "job.id";
    static final String TableID = "job.table-id";
    static final String ResponsibleNode = "job.responsible-node";
  }

  protected final Properties properties = new Properties();

  public Properties getProperties() {
    return this.properties;
  }

  public MapReduceJob(final TableID tableID) {
    this.setTableID(tableID);
  }

  private void setTableID(final TableID tableID) {
    this.properties.put(Property.TableID, tableID);
  }

  @Override
  public TaskID getID() {
    return this.properties.getDynamicCast(Property.JobID, TaskID.class);
  }

  @Override
  public TableID getSourceTableID() {
    return this.properties.getDynamicCast(Property.TableID, TableID.class);
  }

  public void setResponsibleNode(final RemoteNode node) {
    this.properties.put(Property.ResponsibleNode, node);
  }

  @Override
  public RemoteNode getResponsibleNode() {
    return this.properties.getDynamicCast(Property.ResponsibleNode, RemoteNode.class);
  }
}
