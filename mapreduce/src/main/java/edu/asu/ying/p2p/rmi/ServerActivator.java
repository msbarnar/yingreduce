package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;

/**
 *
 */
public interface ServerActivator {

  public enum ActivationMode {
    Singleton,
    SingleCall
  }

  public interface ActivatorBinding<TBindee extends Remote> {

    <TBound extends TBindee> ActivatorBinding to(Class<TBound> type,
                                                 ServerActivator.ActivationMode mode);
    ActivatorBinding toInstance(TBindee instance);
  }

  <TBindee extends Remote> ActivatorBinding bind(Class<TBindee> type);
}
