import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Provider;
import edu.asu.ying.mapreduce.Properties;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.resource.RemoteResources;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import org.junit.Assert;

import java.util.List;


/**
 *
 */
public final class RemoteResourcesTester {
	private final Provider<RemoteResources> getter;
	private int numResults = 0;

	@Inject
	private RemoteResourcesTester(final Provider<RemoteResources> getter) {
		this.getter = getter;
	}

	public void Test() throws Exception {

		final RemoteResources<Activator> remoteActivators = this.getter.get();

		List<ListenableFuture<Activator>> activators =
				remoteActivators.get(
						new ResourceIdentifier("resource\\(10)localhost\\activator"),
						new Properties()
				);

		for (final ListenableFuture<Activator> activator : activators) {
			Futures.addCallback(activator, new FutureCallback<Activator>()
			{
				@Override
				public void onSuccess(final Activator activator) {
					RemoteResourcesTester.this.numResults++;
					Assert.assertNotNull(activator);
					try {
						System.out.println(activator.echo("hi: "));
						Assert.assertNotNull(activator.echo("hi: "));
					} catch (final Throwable e) {
						throw new AssertionError("Activator call failed", e);
					}
				}

				@Override
				public void onFailure(final Throwable throwable) {
					throw new AssertionError("Future failed", throwable);
				}
			});
		}

		System.out.println("Results: ".concat(String.valueOf(this.numResults)));
	}
}
