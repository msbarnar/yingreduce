package edu.asu.ying.test;

import org.junit.Test;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activatable;
import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.ActivatorImpl;

/**
 *
 */
public class TestActivator {

  private interface RemoteCar extends Activatable {

    void honk() throws RemoteException;
  }

  private final class RemoteCarImpl implements RemoteCar {

    private final long timeMade = System.currentTimeMillis();

    RemoteCarImpl() {
      System.out.println("New car smell");
    }

    @Override
    public void honk() throws RemoteException {
      System.out.println("Faraway beep! I was built on " + Long.toString(this.timeMade));
    }
  }

  @Test
  public void itExportsSingleCall() {
    final Activator activator = new ActivatorImpl();

    try {
      activator.bind(RemoteCar.class).to(RemoteCarImpl.class, Activator.ActivationMode.Singleton)
          .honk();
      activator.bind(RemoteCar.class).to(RemoteCarImpl.class, Activator.ActivationMode.SingleCall)
          .honk();
      activator.bind(RemoteCar.class).to(RemoteCarImpl.class, Activator.ActivationMode.SingleCall)
          .honk();
      activator.bind(RemoteCar.class).to(RemoteCarImpl.class, Activator.ActivationMode.Singleton)
          .honk();
    } catch (final RemoteException e) {
      throw new AssertionError("Failed remote call", e);
    }
  }
}
