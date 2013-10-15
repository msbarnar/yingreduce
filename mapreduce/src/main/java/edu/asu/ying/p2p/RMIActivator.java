package edu.asu.ying.p2p;

import com.google.inject.Inject;

import edu.asu.ying.common.remoting.rmi.AbstractRMIActivator;
import edu.asu.ying.p2p.rmi.RMIPort;

/**
 *
 */
public final class RMIActivator extends AbstractRMIActivator {

  @Inject
  private RMIActivator(@RMIPort int port, Channel channel) {
    super(port);
    channel.registerMessageHandler(this, "p2p.activator");
  }
}
