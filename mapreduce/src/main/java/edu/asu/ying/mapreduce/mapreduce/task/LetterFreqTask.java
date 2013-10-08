package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.p2p.RemoteNode;

/**
 *
 */
public final class LetterFreqTask extends TaskBase {

  private final RemoteNode reductionNode;

  private final int index;
  private final File file;

  public LetterFreqTask(final Job parentJob, final RemoteNode reductionNode, final int index) {
    // Task ID = table name + page index
    super(parentJob, new TaskID(parentJob.getTableID().toString().concat(String.valueOf(index))));
    this.reductionNode = reductionNode;
    this.index = index;
    this.file = new File(System.getProperty("user.home").concat("/mapreduce/data/lipsum.txt"));
  }

  public final RemoteNode getReductionNode() {
    return this.reductionNode;
  }

  public Serializable run() {
    BufferedReader reader;

    try {
      reader = new BufferedReader(new FileReader(this.file));
    } catch (final IOException e) {
      e.printStackTrace();
      return null;
    }

    // Count the frequency of each letter
    final int[] freqs = new int[26];

    String line = null;
    // Skip to the Ith line of text in the file ("page in the table")
    for (int i = 0; i < this.index; i++) {
      try {
        /*try {
          Thread.sleep(5);
        } catch (final InterruptedException e) {}*/
        reader.readLine();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    // Read the Ith line (page)
    try {
      line = reader.readLine();
    } catch (final IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    if (line != null) {
      // Alpha only, case insensitive
      line = line.toUpperCase().replaceAll("[^A-Z]", "");
      for (final char c : line.toCharArray()) {
        // Index in the frequency array using the alphabetic index of the character
        final int index = c - 'A';
        if (index >= 0 && index < 26) {
          freqs[index]++;
        }
      }
    }

    // Convert the frequency array to a hashmap for nicer reducing etc.
    final Map<Character, Integer> result = new HashMap<>();
    for (int i = 0; i < freqs.length; i++) {
      result.put((char) ('A' + i), freqs[i]);
    }

    return (Serializable) result;
  }
}
