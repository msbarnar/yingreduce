package edu.asu.ying.mapreduce.net.resources;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Identifies an item on the network. </p> Format: </p> {@code scheme\(replication)host:port\path\name}
 */
public class ResourceIdentifier
    implements Serializable {

  public static final ResourceIdentifier Empty = new ResourceIdentifier();

  private static final long SerialVersionUID = 1L;

  private static final String DELIMITER = "\\";

  protected final Map<String, String> parts = new HashMap<>();
  protected String identifier;

  protected static final int DEFAULT_REPLICATION = 1;
  protected static final int DEFAULT_PORT = -1;

  protected static final class Part {
    public static final String Scheme = "scheme";
    public static final String Replication = "replication";
    public static final String Host = "host";
    public static final String Port = "port";
    public static final String Path = "path";
    public static final String Name = "name";
  }

  private ResourceIdentifier() {
  }

  public ResourceIdentifier(final String identifier) {
    if (Strings.isNullOrEmpty(identifier)) {
      throw new IllegalArgumentException(identifier);
    }
    this.parse(identifier);
  }

  public ResourceIdentifier(final String scheme, final String address) {
    this.setScheme(scheme);
    this.parseAddress(address);
  }

  public ResourceIdentifier(final String scheme, final String host, final int port) {
    this.setScheme(scheme);
    this.setHost(host);
    this.setPort(port);
  }

  public ResourceIdentifier(final String scheme, final String host, final int port,
                            final String path) {

    this(scheme, host, port);
    this.setPath(path);
  }

  public ResourceIdentifier(final String scheme, final String host, final int port,
                            final String path, final String name) {

    this(scheme, host, port, path);
    this.setName(name);
  }

  protected void parse(final String identifier) {
    final Iterator<String> iter = Splitter.on(DELIMITER).trimResults().split(identifier).iterator();

    for (int i = 0; iter.hasNext(); i++) {
      final String part = iter.next();
      // Defines the order of the parts in the identifier
      switch (i) {
        case 0: this.setScheme(part); break;
        case 1: this.parseAddress(part); break;
        case 2: this.setPath(part); break;
        case 3: this.setName(part); break;
      }
    }
  }

  protected void parseAddress(final String address) {
    final List<String> parts = Lists.newArrayList(Splitter.on(':').trimResults().split(address));
    // Try to set the port from the last part
    if (parts.size() > 1) {
      final String szPort = parts.get(parts.size()-1);
      try {
        this.setPort(Integer.parseInt(szPort));
      } catch (final NumberFormatException e) {
        throw new IllegalArgumentException("Invalid port.", e);
      }
    }

    // The point from where we start getting the host
    // If there is a replication string, this will be > 0
    int substrStart = 0;
    // Find the replication at the beginning of the string
    final String firstPart = parts.get(0);
    if (firstPart.charAt(0) == '(') {
      substrStart = 1;
      // Find the closing brace of the replication
      final int replEnd = firstPart.indexOf(')');
      if (replEnd > 0) {
        substrStart = replEnd+1;
        final String szRepl = firstPart.substring(1, replEnd);
        try {
          this.setReplication(Integer.parseInt(szRepl));
        } catch (final NumberFormatException e) {
          throw new IllegalArgumentException("Invalid replication parameter.", e);
        }
      } else {
        throw new IllegalArgumentException("Incomplete replication parameter: ".concat(address));
      }
    }

    // The host is everything from after the first ')' to the first ':'
    this.setHost(firstPart.substring(substrStart));
  }

  protected void clear() {
    this.setScheme("");
    this.setReplication(-1);
    this.setHost("");
    this.setPort(-1);
    this.setPath("");
    this.setName("");
  }

  /**
   * Gets the string value of the specified part or, if the part does not have a value, the empty
   * string.
   */
  private String getPartOrEmpty(final String part) {
    final String value = this.parts.get(part);
    if (value == null) {
      return "";
    } else {
      return value;
    }
  }

  protected final void setScheme(String scheme) {
    if (Strings.isNullOrEmpty(scheme)) {
      throw new IllegalArgumentException("Scheme cannot be null or empty.");
    }
    this.parts.put(Part.Scheme, scheme);
    this.makeIdentifier();
  }
  public final String getScheme() {
    return getPartOrEmpty(Part.Scheme);
  }

  protected final void setHost(String host) {
    if (Strings.isNullOrEmpty(host)) {
      throw new IllegalArgumentException("Host cannot be null or empty.");
    }
    this.parts.put(Part.Host, host);
    this.makeIdentifier();
  }
  public final String getHost() {
    return getPartOrEmpty(Part.Host);
  }

  protected final void setPort(int port) {
    if (port <= 0) {
     port = DEFAULT_PORT;
    }
    this.parts.put(Part.Port, String.valueOf(port));
    this.makeIdentifier();
  }

  public final int getPort() {
    try {
      return Integer.parseInt(this.getPartOrEmpty(Part.Port));
    } catch (final NumberFormatException e) {
      // Default
      this.setPort(DEFAULT_PORT);
      return DEFAULT_PORT;
    }
  }

  public final String getAddress() {
    return this.getHost().concat(":").concat(String.valueOf(this.getPort()));
  }

  protected final void setPath(String path) {
    if (path == null) {
      path = "";
    }
    this.parts.put(Part.Path, path);
    this.makeIdentifier();
  }

  public final String getPath() {
    return getPartOrEmpty(Part.Path);
  }

  protected final void setName(String name) {
    if (name == null) {
      name = "";
    }
    this.parts.put(Part.Name, name);
    this.makeIdentifier();
  }

  public final String getName() {
    return getPartOrEmpty(Part.Name);
  }

  protected final void setReplication(int replication) {
    // It doesn't make any sense to have an address with 0 replication (target nodes)
    if (replication <= 0) {
      replication = DEFAULT_REPLICATION;
    }
    this.parts.put(Part.Replication, String.valueOf(replication));
    this.makeIdentifier();
  }

  public final int getReplication() {
    try {
      return Integer.parseInt(this.getPartOrEmpty(Part.Replication));
    } catch (final NumberFormatException e) {
      // Default
      this.setReplication(DEFAULT_REPLICATION);
      return DEFAULT_REPLICATION;
    }
  }

  protected void makeIdentifier() {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.getScheme());
    sb.append(DELIMITER);
    sb.append('(');
    sb.append(this.getReplication());
    sb.append(')');
    sb.append(this.getAddress());
    sb.append(DELIMITER);
    sb.append(this.getPath());
    sb.append(DELIMITER);
    sb.append(this.getName());

    this.identifier = sb.toString();
  }

  @Override
  public final String toString() {
    if (Strings.isNullOrEmpty(this.identifier)) {
      this.makeIdentifier();
    }
    return this.identifier;
  }

  @Override
  public final boolean equals(final Object rhs) {
    if (this == rhs) {
      return true;
    }
    if (!(rhs instanceof ResourceIdentifier)) {
      return false;
    }

    if (rhs.toString() == null) {
      return this.identifier == null;
    }
    return rhs.toString().equals(this.identifier);
  }

  @Override
  public final int hashCode() {
    return this.identifier.hashCode();
  }
}
