package edu.asu.ying.common.remoting;

import java.rmi.server.ExportException;

/**
 * Specifies a class which exports instances of {@code T} as proxies of type {@code R}.
 */
public interface Exporter<T, R> {

  R export(T target) throws ExportException;
}
