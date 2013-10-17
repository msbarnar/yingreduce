package edu.asu.ying.wellington.dfs.server;

/**
 * {@code PageStreamProvider} provides streams for reading and writing page data from and to
 * storage.
 * </p>
 * If the page is stored on another node, the provider will retrieve it and cache it locally.
 * The provided stream will then be serviced by that cache.
 */
public final class PageStreamProvider {

}
