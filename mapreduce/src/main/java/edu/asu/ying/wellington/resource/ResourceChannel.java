package edu.asu.ying.wellington.resource;

/**
 * {@code ResourceChannel} is the transport layer for the {@link ResourceProvider} to obtain
 * resources.
 * </p>
 * e.g. the RMI provider may use an HTTP resource channel to get RMI proxies from an RMI registry
 * hosted on a webserver.
 */
public interface ResourceChannel {

  Resource get(String name, Class<? extends Resource> cls) throws ResourceException;
}
