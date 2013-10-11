package edu.asu.ying.p2p.rmi;

/**
 * {@code} Specifies a factory that produces wrappers implementing interface {@code K}.
 */
public interface WrapperFactory<T, K> {

  K createWrapper(final T target);
}
