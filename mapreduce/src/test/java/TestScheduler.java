import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.kad.KademliaModule;
import edu.asu.ying.mapreduce.rmi.ActivatorFinder;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.mapreduce.task.Task;

/**
 *
 */
public class TestScheduler implements FutureCallback<Activator> {
  private boolean finished = false;

  private Task toSchedule;

  @Test
  @SuppressWarnings("unchecked")
  public void ItAcceptsTasks() throws Exception {
    // Open a channel with no peers and get an remote; it should be our own
    final KademliaModule channel = new KademliaModule();
    // Get an remote
    final Injector injector = Guice.createInjector(channel);
    // Getting an instance of the local node starts it
    // bind is necessary because the message handlers cannot be bound at construction due to
    // a cyclic dependency.
    final LocalNode node = injector.getInstance(LocalNode.class);
    node.bind();

    // Unchecked
    final ActivatorFinder<Activator> finder = injector.getInstance(ActivatorFinder.class);

    // Bogus remote node, will find the local node as we aren't in any network
    final String host = Base64.encodeBase64String(
        UUID.randomUUID().toString().substring(0, 20).getBytes());
    // URI for the activators at the 3 closest remote nodes
    final ResourceIdentifier hostUri = new ResourceIdentifier(
        String.format("resource\\(3)%s\\remote", host));

    // Find all of the activators that match the URI
    final List<ListenableFuture<Activator>> futures = finder.getFutureResources(hostUri,
                                                                                Properties.Empty);

    // Bind each future's completion to this class
    for (final ListenableFuture<Activator> future : futures) {
      Futures.addCallback(future, this);
    }

    while (!this.finished) Thread.sleep(100);
  }

  /**
   * Called when a {@link ListenableFuture} finishes.
   */
    @Override
    public void onSuccess(Activator activator) {
      try {
        this.doScheduling(activator);
      } catch (final RemoteException e) {
        throw new AssertionError(e);
      }
    }

    @Override
    public void onFailure(Throwable t) {
      if (!(t instanceof CancellationException)) {
        throw new AssertionError(t);
      }
    }

  private void doScheduling(final Activator activator) throws RemoteException {
    Assert.assertNotNull(activator);

    final Scheduler scheduler = activator.createReference(Scheduler.class, null);
    System.out.println("Scheduling mapreduce");
    scheduler.addTask(this.toSchedule);

    this.finished = true;
  }
}
