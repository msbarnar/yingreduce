import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.net.client.LocalNode;
import edu.asu.ying.mapreduce.net.kad.KademliaNetwork;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;
import edu.asu.ying.mapreduce.net.resources.client.RemoteResourceFinder;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.activator.kad.RemoteTest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;


/**
 *
 */
public class TestResourceFinder
{
	@Test
	public void ClientGetsOwnActivator() throws Exception {
		// Open a channel with no peers and get an activator; it should be our own
		final KademliaNetwork channel = new KademliaNetwork();
		// Get an activator
		final Injector injector = Guice.createInjector(channel);
		// Getting an instance of the local node starts it
		final LocalNode node = injector.getInstance(LocalNode.class);
      node.bind();

		final RemoteResourceFinder<Activator> finder = injector.getInstance(RemoteResourceFinder.class);

		final String host = Base64.encodeBase64String(UUID.randomUUID().toString().substring(0, 20).getBytes());
		final ResourceIdentifier hostUri = new ResourceIdentifier("resource", host, -1, "activator");

		final List<ListenableFuture<Activator>> futures = finder.getFutureResources(hostUri, Properties.Empty);
		Thread.sleep(3000);

		for (final ListenableFuture<Activator> future : futures) {
			Assert.assertTrue(future.isDone());
			final Activator activator = future.get();

			if (activator != null) {
				System.out.println("Sending: ".concat(host));
				final String resp = activator.echo(host);
				System.out.println("Response: ".concat(resp));
				Assert.assertEquals(activator.echo(host), host);
				final RemoteTest test = activator.getReference(RemoteTest.class, null);
				System.out.println(test.getString());
			}
		}
	}
}
