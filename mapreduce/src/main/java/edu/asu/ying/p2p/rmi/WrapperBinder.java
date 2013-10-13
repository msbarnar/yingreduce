package edu.asu.ying.p2p.rmi;

import java.rmi.server.ExportException;

public interface WrapperBinder<T, K extends Activatable> {

  /**
   * Makes {@code instance} the target of the {@code via} binding. </p> When the bound type {@code
   * K} of the binding is requested, this binding will provide it by instantiating its wrapper class
   * with {@code instance} as the parameter.
   */
  <W extends WrapperFactory<T, K>> K wrappedBy(W wrapperFactory) throws ExportException;
}
