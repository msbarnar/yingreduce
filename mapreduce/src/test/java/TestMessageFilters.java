import edu.asu.ying.mapreduce.messaging.MessageBase;
import edu.asu.ying.mapreduce.messaging.filter.MessageFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TestMessageFilters
{
	private class TestMessage extends MessageBase {}
	private class TestMessage2 extends MessageBase {}

	@Test
	public void MessageFilteredOnAll() {
		MessageFilter filter = new MessageFilter();
		filter.allOf.id("hi").type(TestMessage.class);

		TestMessage message = new TestMessage();
		message.setId("hi");

		Assert.assertTrue(filter.match(message));

		message.setId("bye");
		Assert.assertFalse(filter.match(message));
	}

	@Test
	public void MessageFilteredOnAny() {
		MessageFilter filter = new MessageFilter();
		filter.anyOf.id("ohno!").type(TestMessage.class);

		TestMessage message = new TestMessage();
		message.setId("hi");

		Assert.assertTrue(filter.match(message));

		TestMessage2 message2 = new TestMessage2();
		message2.setId("ohno!");

		Assert.assertTrue(filter.match(message));

		message2.setId("hi");

		Assert.assertFalse(filter.match(message2));
	}

	@Test
	public void MessageFilteredOnNone() {
		MessageFilter filter = new MessageFilter();
		filter.noneOf.id("2").type(TestMessage.class);

		TestMessage message = new TestMessage();
		message.setId("1");
		TestMessage2 message2 = new TestMessage2();
		message2.setId("2");
		TestMessage2 message3 = new TestMessage2();
		message3.setId("3");

		Assert.assertFalse(filter.match(message));
		Assert.assertFalse(filter.match(message2));
		Assert.assertTrue(filter.match(message3));
	}

	@Test
	public void MessageFilteredOnCompound() {
		MessageFilter filter = new MessageFilter();
		filter.allOf.type(TestMessage.class).noneOf.type(TestMessage2.class).anyOf.id("1").id("2");

		TestMessage message = new TestMessage();

		message.setId("1");
		Assert.assertTrue(filter.match(message));

		message.setId("2");
		Assert.assertTrue(filter.match(message));

		message.setId("3");
		Assert.assertFalse(filter.match(message));

		TestMessage2 message2 = new TestMessage2();
		message2.setId("1");

		Assert.assertFalse(filter.match(message2));
	}
}
