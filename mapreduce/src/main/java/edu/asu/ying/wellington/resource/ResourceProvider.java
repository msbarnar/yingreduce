package edu.asu.ying.wellington.resource;

/**
 * {@code ResourceProvider} is the interface by which the {@code Transport} layer obtains resources
 * to share.
 * </p>
 * When a request for a service is placed at the {@link ServiceLocator}, the request is passed to
 * the {@code Resource} layer which manages the implementation of the service abstraction. The
 * {@code Resource} layer fulfills that request by obtaining the proper instance of the named
 * resource from the {@code Transport} layer.
 */
public interface ResourceProvider {
}
