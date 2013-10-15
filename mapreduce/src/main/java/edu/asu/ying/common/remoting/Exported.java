package edu.asu.ying.common.remoting;

/**
 * Specifies that the implementing class exports itself as the remote type {@code R}. </p> {@code
 * Exported} classes provide access to their instances via a remote proxy. That proxy is generally
 * obtained by passing the class through an {@link edu.asu.ying.common.remoting.Exporter}.
 */
public interface Exported<R extends Activatable> {

  R asRemote();
}
