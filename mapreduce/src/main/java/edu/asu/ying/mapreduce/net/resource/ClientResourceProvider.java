package edu.asu.ying.mapreduce.net.resource;

import com.google.common.util.concurrent.ListenableFuture;
import edu.asu.ying.mapreduce.common.Properties;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


/**
 * The {@code ClientResourceProvider} is responsible for getting remote resources from another node.
 */
public interface ClientResourceProvider<V>
{
	List<ListenableFuture<V>> getFutureResources(final ResourceIdentifier uri, final Properties args)
			throws URISyntaxException, IOException;
}
