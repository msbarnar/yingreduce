import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;


/**
 *
 */
public class TestResourceIdentifier
{
	@Test
	public void HostAndPort() throws URISyntaxException {
		final int randPort = 1 + (new Random()).nextInt(10000);
		final String randHost = UUID.randomUUID().toString();

		final String test = String.format("scheme/%s:%d/path/name", randHost, randPort);
		final ResourceIdentifier uri = new ResourceIdentifier(test);

		Assert.assertEquals("scheme", uri.getScheme());
		Assert.assertEquals(randHost, uri.getHost());
		Assert.assertEquals(randPort, uri.getPort());
		Assert.assertEquals("path", uri.getPath());
		Assert.assertEquals("name", uri.getName());
	}

	@Test
	public void HostOnly() throws URISyntaxException {
		final String randHost = UUID.randomUUID().toString();
		final String test = String.format("scheme/%s/path/name", randHost);
		final ResourceIdentifier uri = new ResourceIdentifier(test);

		Assert.assertEquals("scheme", uri.getScheme());
		Assert.assertEquals(randHost, uri.getHost());
		Assert.assertEquals(-1, uri.getPort());
		Assert.assertEquals("path", uri.getPath());
		Assert.assertEquals("name", uri.getName());
	}

	@Test
	public void HostTwoPorts() throws URISyntaxException {
		final int randPort = 1 + (new Random()).nextInt(10000);
		final String randHost = UUID.randomUUID().toString();

		final String test = String.format("scheme/%s:50:%d/path/name", randHost, randPort);
		final ResourceIdentifier uri = new ResourceIdentifier(test);

		Assert.assertEquals("scheme", uri.getScheme());
		Assert.assertEquals(randHost, uri.getHost());
		Assert.assertEquals(randPort, uri.getPort());
		Assert.assertEquals("path", uri.getPath());
		Assert.assertEquals("name", uri.getName());
	}
}
