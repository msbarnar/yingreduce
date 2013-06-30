import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.kad.KademliaNetwork;
import edu.asu.ying.mapreduce.net.resource.ResourceIdentifier;
import edu.asu.ying.mapreduce.net.resource.client.RemoteResourceFinder;
import edu.asu.ying.mapreduce.rmi.Activator;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;


/**
 *
 */
public class TestResourceFinder
  implements FutureCallback<Activator>
{
  @Test
  @SuppressWarnings("unchecked")
  public void ClientGetsOwnActivator() throws Exception {
    // Open a channel with no peers and get an activator; it should be our own
    final KademliaNetwork channel = new KademliaNetwork();
    // Get an activator
    final Injector injector = Guice.createInjector(channel);
    // Getting an instance of the local node starts it
    // bind is necessary because the message handlers cannot be bound at construction due to
    // a cyclic dependency.
    final LocalNode node = injector.getInstance(LocalNode.class);
    node.bind();

    // Unchecked
    final RemoteResourceFinder<Activator> finder = injector.getInstance(RemoteResourceFinder.class);

    final String host = Base64.encodeBase64String(
        UUID.randomUUID().toString().substring(0, 20).getBytes());
    final ResourceIdentifier hostUri = new ResourceIdentifier("resource", host, -1, "activator");

    final List<ListenableFuture<Activator>> futures = finder.getFutureResources(hostUri,
                                                                                Properties.Empty);

    for (final ListenableFuture<Activator> future : futures) {
      final Activator activator = future.get();
      Assert.assertNotNull(activator);
    }
  }

  @Override
  public void onSuccess(Activator result) {
  }

  @Override
  public void onFailure(Throwable t) {
  }
}
