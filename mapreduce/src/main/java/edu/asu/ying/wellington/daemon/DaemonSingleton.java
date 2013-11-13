package edu.asu.ying.wellington.daemon;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 *
 */
public class DaemonSingleton {

  private static final Map<Integer, DaemonSingleton> instances = new HashMap<>();

  public static DaemonSingleton get(final int port) {
    DaemonSingleton instance = instances.get(port);
    if (instance == null) {
      synchronized (instances) {
        if (instances.get(port) == null) {
          instance = new DaemonSingleton();
          instances.put(port, instance);
        }
      }
    }
    return instance;
  }

  private String id = null;
  private final Map<String, Long> jobs = new HashMap<>();
  private final Map<String, Integer> tables = new HashMap<>();

  private DaemonSingleton() {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    this.id = byteArrayToHexString(md.digest(UUID.randomUUID().toString().getBytes()));

    this.tables.put("lipsum", 2 + (new Random()).nextInt(3));
  }

  public final void addJob(final String jobId) {
    this.jobs.put(jobId, System.currentTimeMillis());
  }

  public final Map<String, String> getJobs() {
    final Map<String, String> j = new HashMap<>(this.jobs.size());
    for (final String job : this.jobs.keySet()) {
      j.put(job, (System.currentTimeMillis() - this.jobs.get(job) < 4000) ? "running" : "complete");
    }

    return j;
  }

  public final Map<String, Integer> getTables() {
    return this.tables;
  }

  public final void setId(final String id) {
    this.id = id;
  }

  public final String getId() {
    return this.id;
  }

  private static String byteArrayToHexString(byte[] b) {
    String result = "";
    String samp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_+=";
    for (int i = 0; i < Math.min(b.length, 22); i++) {
      int j = (b[i] & 0xFF) % samp.length();
      result += samp.substring(j, j + 1);
    }
    return result;
  }
}
