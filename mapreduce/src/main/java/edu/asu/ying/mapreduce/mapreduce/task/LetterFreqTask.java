package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.map.MapTask;
import edu.asu.ying.p2p.RemoteNode;

/**
 *
 */
public class LetterFreqTask extends MapTask {

  private final int index;
  private final File file;

  public LetterFreqTask(final Job parentJob, final RemoteNode reductionNode, final int index) {
    super(parentJob, reductionNode);
    this.index = index;
    this.file = new File("lipsum.txt");
  }

  @Override
  public Serializable run() {
    BufferedReader reader;

    try {
      reader = new BufferedReader(new FileReader(this.file));
    } catch (final IOException e) {
      return null;
    }

    final int[] freqs = new int[26];

    String line = null;
    for (int i = 0; i < this.index; i++) {
      try {
        reader.readLine();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    do {
      try {
        line = reader.readLine();
      } catch (final IOException e) {
        e.printStackTrace();
      }

      if (line != null) {
        line = line.toUpperCase();
        for (final char c : line.toCharArray()) {
          final int index = c - 'A';
          if (index > 0 && index < 26) {
            freqs[index]++;
          }
        }
      }

    } while (line != null);

    final Map<Character, Integer> result = new HashMap<>();
    for (int i = 0; i < freqs.length; i++) {
      result.put((char) ('A' + i), freqs[i]);
    }

    return (Serializable) result;
  }
}
