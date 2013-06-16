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
import edu.asu.ying.mapreduce.rmi.resource.ResourceResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;


/**
 * Installs a mock {@link edu.asu.ying.mapreduce.io.MessageOutputStream} that tests instead of sending data.
 */
public class TestRemoteResources
		extends AbstractModule
{
	// Simulates a message handler getting messages from the network
	private final FilteredValueEvent<Message> onIncomingMessages = new FilteredValueEvent<>();

	private class TestActivator implements Activator {
		@Override
		public <T extends Remote> T getReference(final Class<T> type, final Map<String, String> properties)
				throws RemoteException { return null; }
		@Override
		public String echo(final String message) throws RemoteException { return message; }
		@Override
		public ResourceIdentifier getResourceUri() throws RemoteException { return null; }
	}
	private class TestOutputStream implements MessageOutputStream {
		@Override
		public int write(final Message message) throws IOException {
			System.out.println("Message written: ".concat(message.getClass().getName()));
			TestRemoteResources.this.onIncomingMessages.fire(this, new ResourceResponse(message, new TestActivator()));
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
			Activator ac = activator.get();
			Assert.assertNotNull(ac);
			System.out.println("Echo: ".concat(ac.echo("hi")));
		}
	}
}
