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

  public interface Binding<TBound extends Remote> {
    TBound getReference();
  }

  public interface Binder<TBound extends Remote> {

    <TBindee extends TBound> void to(Class<TBindee> type,
                                        ServerActivator.ActivationMode mode);
    void toInstance(TBound instance);

    Binding getBinding();
  }

  <TBound extends Remote> Binder bind(Class<TBound> type);

  RemoteActivator export();
}
