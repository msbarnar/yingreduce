package edu.asu.ying.daemon.web;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 */
public final class RootRestlet extends ServerResource {

  @Get
  public final String represent() {
    return "YingReduce \u00A9 2013 Ying Lab, Arizona State University";
  }
}
