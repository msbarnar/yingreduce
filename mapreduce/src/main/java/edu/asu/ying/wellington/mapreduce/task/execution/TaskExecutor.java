package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.asu.ying.test.ExampleCollector;
import edu.asu.ying.test.ExampleMapReduceJob;
import edu.asu.ying.test.ExampleReporter;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.RemotePage;
import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.io.WritableString;
import edu.asu.ying.wellington.mapreduce.OutputCollector;
import edu.asu.ying.wellington.mapreduce.Reporter;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public class TaskExecutor {

  private static final Logger log = Logger.getLogger(TaskExecutor.class);

  private final DFSService dfs;

  private long timeSlotSecond = -1;
  private long timeSlotStart = 0;


  @Inject
  private TaskExecutor(DFSService dfs) {
    this.dfs = dfs;
  }

  public synchronized void execute(Task task) {
    if (timeSlotSecond < 0) {
      timeSlotStart = System.currentTimeMillis();
    }

    PageName pageName = task.getTargetPageID();

    long start = System.currentTimeMillis();

    if (!dfs.hasPage(pageName)) {
      try {
        RemotePage page = dfs.fetchRemotePage(pageName);

      } catch (IOException e) {
        log.error("Exception getting remote page for task", e);
      }
    }

    ExampleMapReduceJob.LetterCounter taskImpl = new ExampleMapReduceJob.LetterCounter();

    OutputCollector<WritableChar, WritableInt> collector = new ExampleCollector(task);
    Reporter reporter = new ExampleReporter(task);

    try (InputStreamReader reader = new InputStreamReader(
        dfs.getPersistence().readPage(pageName))) {
      WritableString key = new WritableString(task.getTargetPageID().path().toString());

      StringBuilder sb = new StringBuilder();

      int readTotal = 0;

      char[] buf = new char[1024];
      int read;
      while ((read = reader.read(buf, 0, buf.length)) > 0) {
        sb.append(buf, 0, read);
        readTotal += read;
      }

      taskImpl.map(key, new WritableString(sb.toString()), collector, reporter);

      timeSlotSecond = (long) Math.floor((System.currentTimeMillis() - timeSlotStart));

      try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"/tasktimes.csv", true))) {
        bw.write(String.format("%d,%d\n", timeSlotSecond, readTotal));
      } catch (IOException e) {
        e.printStackTrace();
      }


      /*System.out.println(String.format("%s,%s,%d,%f", task.getParentJob().getName(),
                                       task.getTargetPageID().path(),
                                       task.getTargetPageID().index(),
                                       (System.currentTimeMillis() - start) * 0.001));*/

    } catch (IOException e) {
      log.error("Exception reading page for task", e);
    }
  }
}
