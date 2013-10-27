package edu.asu.ying.wellington;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.util.Properties;

/**
 */
public final class WellingtonModule extends AbstractModule {

  private final Properties properties;

  public WellingtonModule() {
    this(new Properties());
  }

  public WellingtonModule(Properties properties) {
    this.properties = getDefaultProperties();
    this.properties.putAll(properties);
  }

  public WellingtonModule setProperty(String key, String value) {
    this.properties.setProperty(key, value);
    return this;
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties);
  }

  private Properties getDefaultProperties() {
    Properties defaults = new Properties();

    defaults.put("dfs.store.path", System.getProperty("user.home").concat("/dfs"));
    defaults.put("dfs.page.replication", "3");

    return defaults;
  }
}
