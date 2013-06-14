import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.asu.ying.mapreduce.UriUtils;
import edu.asu.ying.mapreduce.channels.kad.KadChannel;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.resource.ResourceFinder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
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
		String host = UriUtils.encodeHost(UUID.randomUUID().toString().substring(0, 20));
		final Activator activator = (Activator) finder.findResource(
				URI.create("resource://".concat(host).concat("/activator")));

		Assert.assertEquals(activator.echo(host), host);
	}
}
