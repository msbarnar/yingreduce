package edu.asu.ying.wellington.mapreduce.job;

import edu.asu.ying.wellington.Mappable;
import edu.asu.ying.wellington.Reducer;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

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
