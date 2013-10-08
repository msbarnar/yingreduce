package edu.asu.ying.mapreduce.daemon.web;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import edu.asu.ying.mapreduce.daemon.DaemonInterface;

/**
 *
 */
public final class RestInterface extends Application
    implements DaemonInterface {

  private final Component component = new Component();

  public RestInterface(final int port) {
    this.component.getServers().add(Protocol.HTTP, port);
    this.component.getDefaultHost().attach(this);
  }

  @Override
  public void startInterface() throws Exception {
    this.component.start();
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(this.getContext());
    router.attachDefault(RootRestlet.class);
    router.attach("/daemon", DaemonStatusRestlet.class);
    router.attach("/job/create", JobCreateRestlet.class);
    router.attach("/job/{jobid}", JobStatusRestlet.class);
    return router;
  }
}
