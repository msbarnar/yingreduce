package edu.asu.ying.wellington.mapreduce.job;

import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.mapreduce.Mappable;
import edu.asu.ying.wellington.mapreduce.Reducer;

/**
 *
 */
public final class JobConf implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final String tableName;

  private Class<? extends WritableComparable> outputKeyClass;
  private Class<? extends Writable> outputValueClass;
  private Class<? extends Mappable> mapperClass;
  private Class<? extends Reducer> reducerClass;

  public JobConf(String tableName) {
    this.tableName = tableName;
  }

  public <T extends WritableComparable> JobConf setOutputKeyClass(final Class<T> keyClass) {
    this.outputKeyClass = keyClass;
    return this;
  }

  public <T extends Writable> JobConf setOutputValueClass(final Class<T> valueClass) {
    this.outputValueClass = valueClass;
    return this;
  }

  public <T extends Mappable> JobConf setMapperClass(final Class<T> mapperClass) {
    this.mapperClass = mapperClass;
    return this;
  }

  public <T extends Reducer> JobConf setReducerClass(final Class<T> reducerClass) {
    this.reducerClass = reducerClass;
    return this;
  }

  public String getTableName() {
    return tableName;
  }
}
