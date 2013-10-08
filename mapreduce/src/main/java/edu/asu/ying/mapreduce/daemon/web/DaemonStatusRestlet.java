package edu.asu.ying.mapreduce.daemon.web;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 */
public final class DaemonStatusRestlet extends ServerResource {

  @Get
  public final String represent() {
    return "Hello, world!";
  }
}
