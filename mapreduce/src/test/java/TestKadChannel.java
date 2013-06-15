import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.asu.ying.mapreduce.channels.kad.KadChannel;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.rmi.activator.kad.RemoteTest;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.resource.ResourceFinder;
import org.apache.commons.codec.binary.Base64;
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
		final String host = Base64.encodeBase64String(UUID.randomUUID().toString().substring(0, 20).getBytes());
		final ResourceIdentifier hostUri = new ResourceIdentifier("resource", host, -1, "activator");
		final Activator activator = (Activator) finder.findResource(hostUri);

		Assert.assertNotEquals(activator, null);

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
