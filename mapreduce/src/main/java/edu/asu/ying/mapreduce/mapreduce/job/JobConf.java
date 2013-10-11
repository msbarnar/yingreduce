package edu.asu.ying.mapreduce.mapreduce.job;

import edu.asu.ying.database.io.Writable;
import edu.asu.ying.database.io.WritableComparable;
import edu.asu.ying.mapreduce.Mappable;
import edu.asu.ying.mapreduce.Reducer;

/**
 *
 */
public final class JobConf {

  <T extends WritableComparable> void setOutputKeyClass(final Class<T> keyClass) {
  }

  <T extends Writable> void setOutputValueClass(final Class<T> valueClass) {
  }

  <T extends Mappable> void setMapperClass(final Class<T> mapperClass) {
  }

  <T extends Reducer> void setReducerClass(final Class<T> reducerClass) {
  }
}
