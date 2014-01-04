package edu.asu.ying.test;

import org.apache.log4j.Logger;

import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.mapreduce.OutputCollector;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public class ExampleCollector implements OutputCollector<WritableChar, WritableInt> {

  private static final Logger log = Logger.getLogger(ExampleCollector.class);

  public ExampleCollector(Task task) {
  }

  @Override
  public void collect(WritableChar key, WritableInt value) {
    log.info("Collected " + key.toString() + " = " + value.toString());
  }
}
