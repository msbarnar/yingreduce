package edu.asu.ying.test;

import org.junit.Assert;
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

    String honk() throws RemoteException;
  }

  public static class Car {

    String honk() {
      return "beep!";
    }
  }

  private class CarWrapper implements RemoteCar {

    private final Car car;

    public CarWrapper(final Car car) {
      this.car = car;
    }

    @Override
    public String honk() throws RemoteException {
      return "Faraway " + this.car.honk();
    }
  }

  public class CarWrapperFactory implements WrapperFactory<Car, RemoteCar> {

    @Override
    public RemoteCar createWrapper(Car target) {
      return new CarWrapper(target);
    }
  }

  @Test
  public void itBindsAndCalls() {
    final Activator activator = new ActivatorImpl();

    try {
      Car myCar = new Car();
      RemoteCar
          remoteCar =
          activator.bind(RemoteCar.class).to(myCar).wrappedBy(new CarWrapperFactory());
      Assert.assertEquals("beep!", myCar.honk());
      Assert.assertEquals("Faraway beep!", remoteCar.honk());
    } catch (final RemoteException e) {
      throw new AssertionError("Failed remote call", e);
    }
  }
}
