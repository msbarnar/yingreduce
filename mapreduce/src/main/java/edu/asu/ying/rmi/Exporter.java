package edu.asu.ying.rmi;

import java.rmi.server.ExportException;

/**
 * Specifies a class which exports instances of {@code T} as proxies of type {@code R}.
 */
public interface Exporter<T, R> {

  R export(T target) throws ExportException;
}
