package edu.asu.ying.test;

import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.io.WritableString;
import edu.asu.ying.wellington.mapreduce.Mappable;
import edu.asu.ying.wellington.mapreduce.OutputCollector;
import edu.asu.ying.wellington.mapreduce.Reducer;
import edu.asu.ying.wellington.mapreduce.Reporter;
import edu.asu.ying.wellington.mapreduce.job.JobConf;

/**
 *
 */
public class ExampleMapReduceJob {

  public static JobConf createJob() {
    JobConf job = new JobConf("lipsum");
    job.setOutputKeyClass(WritableChar.class);
    job.setOutputValueClass(WritableInt.class);
    job.setMapperClass(LetterCounter.class);
    job.setReducerClass(LetterCounter.class);
    return job;
  }

  public static class LetterCounter
      implements Mappable<WritableString, WritableString, WritableChar, WritableInt>,
                 Reducer<WritableChar, WritableInt, WritableChar, WritableInt> {

    public LetterCounter() {
    }

    @Override
    public void map(WritableString key, WritableString value,
                    OutputCollector<WritableChar, WritableInt> output, Reporter reporter) {

      Map<Character, Integer> freqs = new HashMap<>();
      for (Character c : value) {
        Integer freq = freqs.get(c);
        if (freq == null) {
          freq = 1;
        } else {
          freq++;
        }
        freqs.put(c, freq);
      }

      for (Map.Entry<Character, Integer> entry : freqs.entrySet()) {
        output.collect(new WritableChar(entry.getKey()), new WritableInt(entry.getValue()));
      }

      output.complete();
    }

    @Override
    public void reduce(WritableChar key, Iterable<WritableInt> values,
                       OutputCollector<WritableChar, WritableInt> output, Reporter reporter) {

      int totalFreq = 0;
      for (WritableInt value : values) {
        totalFreq += value.get();
      }

      output.collect(key, new WritableInt(totalFreq));
    }
  }
}
