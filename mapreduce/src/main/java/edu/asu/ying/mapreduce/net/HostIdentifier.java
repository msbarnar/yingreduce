package edu.asu.ying.mapreduce.net;

import java.net.InetAddress;

import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;

/**
 * {@code HostIdentifier} is a {@link ResourceIdentifier} that identifies a host in the network
 * by its IP address.
 */
public class HostIdentifier extends ResourceIdentifier {

  private static final String HOST_SCHEME = "ip";

  public HostIdentifier(final InetAddress address) {
    super(HOST_SCHEME, address.getHostAddress(), -1);
  }
}
