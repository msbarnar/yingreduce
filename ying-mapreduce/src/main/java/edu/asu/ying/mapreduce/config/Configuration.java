/*
 * DaemonConfiguration.java
 * Provides configuration settings for the entire mapreduce daemon process.
 */
package edu.asu.ying.mapreduce.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Properties;
// For path joining
import org.apache.commons.io.FilenameUtils;
import edu.asu.ying.mapreduce.logging.Logger;


// Configuration provider factory
public class Configuration
{
	// Search directory for configuration files
	private static final String CONFIG_PATH = "config/";
	// Configuration file extension
	private static final String CONFIG_EXTENSION = ".conf";
	
	// Stores loaded configurations
	private static final HashMap<String, Properties> configProps = new HashMap<String, Properties>();
	
	private static final Properties load(final String configName) throws IOException, FileNotFoundException {
		// Try to load the new configuration and cache it
		Properties newConfig = new Properties();
		// Search for .properties files in the config path
		String path = FilenameUtils.concat(CONFIG_PATH, configName + CONFIG_EXTENSION);
		// Find the configuration file on the classpath
		InputStream stream = Configuration.class.getClassLoader().getResourceAsStream(path);
		if (stream == null) {
			throw new FileNotFoundException(String.format("No configuration file `%s`", path));
		}
		// Throws IOException, leaving existing config in place
		newConfig.load(stream);
		configProps.put(configName, newConfig);
		
		Logger.get().fine(String.format("Loaded configuration file `%s`", path));
		return configProps.get(configName);
	}
	
	public static final Properties get(final String configName) throws IOException, FileNotFoundException {
		// Return the configuration, loading if necessary
		Properties config = configProps.get(configName);
		if (config == null) {
			config = load(configName);
		}
		return config;
	}
	
	public static final Properties get(final String configName, final boolean reload) throws IOException, FileNotFoundException {
		// If reload is true, load the configuration file even if it's already loaded.
		if (reload) {
			return load(configName);
		} else {
			return get(configName);
		}
	}
	
	public static final String get(final String configName, final String key) throws IOException, FileNotFoundException {
		return get(configName).getProperty(key);
	}
	
	public static final String get(final String configName, final String key, final boolean reload) throws IOException, FileNotFoundException {
		return get(configName, reload).getProperty(key);
	}
}
