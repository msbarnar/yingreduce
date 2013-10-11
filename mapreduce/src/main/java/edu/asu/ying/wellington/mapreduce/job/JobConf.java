package edu.asu.ying.wellington.mapreduce.job;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.mapreduce.Mappable;
import edu.asu.ying.wellington.mapreduce.Reducer;

/**
 *
 */
public final class JobConf {

  public <T extends WritableComparable> void setOutputKeyClass(final Class<T> keyClass) {
  }

  public <T extends Writable> void setOutputValueClass(final Class<T> valueClass) {
  }

  public <T extends Mappable> void setMapperClass(final Class<T> mapperClass) {
  }

  public <T extends Reducer> void setReducerClass(final Class<T> reducerClass) {
  }
}
