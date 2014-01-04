package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.test.ExampleCollector;
import edu.asu.ying.test.ExampleMapReduceJob;
import edu.asu.ying.test.ExampleReporter;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.io.WritableString;
import edu.asu.ying.wellington.mapreduce.OutputCollector;
import edu.asu.ying.wellington.mapreduce.Reporter;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public final class LocalQueueExecutor extends QueueExecutor<Task> {

  private static final Logger log = Logger.getLogger(LocalQueueExecutor.class);

  private final DFSService dfs;

  @Inject
  private LocalQueueExecutor(DFSService dfs) {
    this.dfs = dfs;
  }

  @Override
  protected void process(Task task) {
    PageName pageName = task.getTargetPageID();

    log.info("Local: " + pageName);

    if (!dfs.hasPage(pageName)) {
      log.error("Node doesn't have page for local task: " + pageName);
    }

    ExampleMapReduceJob.LetterCounter taskImpl = new ExampleMapReduceJob.LetterCounter();

    OutputCollector<WritableChar, WritableInt> collector = new ExampleCollector(task);
    Reporter reporter = new ExampleReporter(task);

    try (InputStreamReader reader = new InputStreamReader(
        dfs.getPersistence().readPage(pageName))) {
      WritableString key = new WritableString(task.getTargetPageID().path().toString());

      char[] buf = new char[1024];
      int read;
      while ((read = reader.read(buf, 0, buf.length)) > 0) {
        WritableString value = new WritableString(new String(buf, 0, read));
        taskImpl.map(key, value, collector, reporter);
      }

    } catch (IOException e) {
      log.error("Exception reading local page for task", e);
    }
  }
}
