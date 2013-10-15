package edu.asu.ying.common.remoting;

import java.rmi.server.ExportException;

/**
 *
 */
public interface Exporter<T, R> {

  R export(T target) throws ExportException;
}
