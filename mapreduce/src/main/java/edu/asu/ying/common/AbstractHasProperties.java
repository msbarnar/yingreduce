package edu.asu.ying.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class AbstractHasProperties<K> {

  protected final Map<K, String> properties = new HashMap<>();

  public void setProperty(K key, String value) {
    this.properties.put(key, value);
  }

  public void setProperty(K key, int value) {
    this.setProperty(key, Integer.toString(value));
  }

  public void setProperty(K key, boolean value) {
    this.setProperty(key, Boolean.toString(value));
  }
}
