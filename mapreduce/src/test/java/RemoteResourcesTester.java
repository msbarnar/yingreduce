import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Provider;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.rmi.ActivatorFinder;
import edu.asu.ying.mapreduce.rmi.Activator;

import org.junit.Assert;

import java.util.List;


/**
 *
 */
public final class RemoteResourcesTester {
	private final Provider<ActivatorFinder<Activator>> getter;
	private int numResults = 0;

	@Inject
	private RemoteResourcesTester(final Provider<ActivatorFinder<Activator>> getter) {
		this.getter = getter;
	}

	public void Test() throws Exception {

		final ActivatorFinder<Activator> remoteActivators = this.getter.get();

		List<ListenableFuture<Activator>> activators =
				remoteActivators.getFutureResources(
						new ResourceIdentifier("resource\\(50)localhost\\remote"),
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
