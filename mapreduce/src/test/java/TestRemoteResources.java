import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import edu.asu.ying.mapreduce.Properties;
import edu.asu.ying.mapreduce.events.FilteredValueEvent;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.messaging.IncomingMessages;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.SendMessageStream;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.resource.RemoteResources;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;


/**
 * Installs a mock {@link edu.asu.ying.mapreduce.io.MessageOutputStream} that tests instead of sending data.
 */
public class TestRemoteResources
		extends AbstractModule
{
	// Simulates a message handler getting messages from the network
	private final FilteredValueEvent<Message> onIncomingMessages = new FilteredValueEvent<>();

	private class TestOutputStream implements MessageOutputStream {
		@Override
		public int write(final Message message) throws IOException {
			System.out.println("Message written: ".concat(message.getClass().getName().toString()));
			return message.getReplication();
		}
	}

	@Override
	protected void configure() {
		bind(MessageOutputStream.class).annotatedWith(SendMessageStream.class).toInstance(new TestOutputStream());
	}

	@Provides
	@IncomingMessages
	private FilteredValueEvent<Message> provideIncomingMessagesEvent() {
		return this.onIncomingMessages;
	}

	@Test
	public void ItSendsRequests() throws Exception {
		final Injector injector = Guice.createInjector(this);
		RemoteResources<Activator> getter = injector.getInstance(RemoteResources.class);

		List<ListenableFuture<Activator>> activators =
				getter.get(
					new ResourceIdentifier("resource\\localhost\\activator"),
					new Properties()
				);

		for (final ListenableFuture<Activator> activator : activators) {
			Assert.assertNotNull(activator.get());
		}
	}
}
