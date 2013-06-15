import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.asu.ying.mapreduce.channels.kad.KadChannel;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.resource.ResourceFinder;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;


/**
 *
 */
public class TestKadChannel
{
	@Test
	public void ClientGetsOwnActivator() throws Exception {
		// Open a channel with no peers and get an activator; it should be our own
		final KadChannel channel = new KadChannel();
		// Get an activator
		final Injector injector = Guice.createInjector(channel);
		// Getting an instance of the local node starts it
		final LocalNode node = injector.getInstance(LocalNode.class);

		final ResourceFinder finder = injector.getInstance(ResourceFinder.class);
		final String host = UUID.randomUUID().toString().substring(0, 20);
		final ResourceIdentifier hostUri = new ResourceIdentifier(String.format("resource/%s/activator", host));
		final Activator activator = (Activator) finder.findResource(hostUri);

		Assert.assertEquals(activator.echo(host), host);
	}
}
