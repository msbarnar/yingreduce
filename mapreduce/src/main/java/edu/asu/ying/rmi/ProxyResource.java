package edu.asu.ying.rmi;

import com.google.common.base.Preconditions;

import edu.asu.ying.wellington.resource.Resource;

/**
 * {@code ProxyResource} enables the resource provider to exchange RMI proxies through a resource
 * channel.
 */
public final class ProxyResource implements Resource {

  private static final long SerialVersionUID = 1L;

  private final Remote proxy;

  public ProxyResource(Remote proxy) {
    this.proxy = Preconditions.checkNotNull(proxy);
  }

  public Remote getProxy() {
    return this.proxy;
  }
}
