package edu.asu.ying.wellington.mapreduce.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 *
 */
public final class LetterFreqTask extends Task {

  private final RemoteNode reductionNode;

  private final int index;
  private final File file;

  public LetterFreqTask(Job parentJob, RemoteNode reductionNode, int index) {
    // Task ID = table name + page index
    super(parentJob, TaskIdentifier.random(), PageIdentifier.create(parentJob.getTableID(), index));
    this.reductionNode = reductionNode;
    this.index = index;
    this.file = new File(System.getProperty("user.home").concat("/mapreduce/data/lipsum.txt"));
  }

  public RemoteNode getReductionNode() {
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
    int[] freqs = new int[26];

    String line = null;
    // Skip to the Ith line of text in the file ("page in the table")
    for (int i = 0; i < this.index; i++) {
      try {
        /*try {
          Thread.sleep(5);
        } catch (final InterruptedException e) {}*/
        reader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Read the Ith line (page)
    try {
      line = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    if (line != null) {
      // Alpha only, case insensitive
      line = line.toUpperCase().replaceAll("[^A-Z]", "");
      for (char c : line.toCharArray()) {
        // Index in the frequency array using the alphabetic index of the character
        int index = c - 'A';
        if (index >= 0 && index < 26) {
          freqs[index]++;
        }
      }
    }

    // Convert the frequency array to a hashmap for nicer reducing etc.
    Map<Character, Integer> result = new HashMap<>();
    for (int i = 0; i < freqs.length; i++) {
      result.put((char) ('A' + i), freqs[i]);
    }

    return (Serializable) result;
  }
}
