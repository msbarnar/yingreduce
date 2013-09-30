package edu.asu.ying.mapreduce.common;

import java.io.Serializable;
import java.rmi.Remote;

/**
 *
 */
public interface RemoteSink<E> extends Sink<E>, Remote, Serializable {
}
