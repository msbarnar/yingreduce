import edu.asu.ying.mapreduce.messaging.MessageBase;
import edu.asu.ying.mapreduce.messaging.filter.MessageFilter;
import edu.asu.ying.mapreduce.messaging.filter2.*;
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
	public void MessageFilterById() {
		TestMessage msg1 = new TestMessage();
		msg1.setId("1");
		msg1.getProperties().put("msg", "1");

		TestMessage2 msg2 = new TestMessage2();
		msg2.setId("2");
		msg2.getProperties().put("msg", "2");

		Assert.assertTrue(MessageFilter2.id("1").filter(msg1));
		Assert.assertFalse(MessageFilter2.id("2").filter(msg1));
	}

	@Test
	public void MessageFilterByType() {
		TestMessage msg1 = new TestMessage();
		TestMessage2 msg2 = new TestMessage2();

		Assert.assertTrue(MessageFilter2.type(TestMessage.class).filter(msg1));
		Assert.assertFalse(MessageFilter2.type(TestMessage.class).filter(msg2));
		Assert.assertTrue(MessageFilter2.type(TestMessage2.class).filter(msg2));
	}

	@Test
	public void MessageFiltersChainTogether() {
		TestMessage msg1 = new TestMessage();
		msg1.setId("1");

		Assert.assertTrue(MessageFilter2.id("1").or.id("2").filter(msg1));
		Assert.assertFalse(MessageFilter2.id("3").or.id("2").filter(msg1));
		Assert.assertTrue(MessageFilter2.id("3").or.id("2").or.id("1").and.id("1").filter(msg1));

		Assert.assertFalse(MessageFilter2.id("1").and.id("2").filter(msg1));
		Assert.assertTrue(MessageFilter2.id("1").not.id("2").filter(msg1));
		Assert.assertFalse(MessageFilter2.id("1").not.id("1").filter(msg1));

		Assert.assertTrue(MessageFilter2.not.id("2").or.id("3").filter(msg1));
		Assert.assertTrue(MessageFilter2.not.id("2").and.id("1").filter(msg1));
		Assert.assertFalse(MessageFilter2.not.id("1").or.id("1").filter(msg1));

		TestMessage msg2 = new TestMessage();
		msg1.setId("2");
		TestMessage msg3 = new TestMessage();
		msg1.setId("3");

		Assert.assertTrue(MessageFilter2.id("1").not.id("2").or.id("3").filter(msg1));
		Assert.assertFalse(MessageFilter2.id("1").not.id("2").or.id("3").filter(msg2));
		Assert.assertFalse(MessageFilter2.id("1").not.id("2").or.id("3").filter(msg3));
	}

	@Test
	public void TrailingMessageFiltersAreIgnored() {
		TestMessage msg1 = new TestMessage();

		Assert.assertTrue(MessageFilter2.type(TestMessage.class).or.filter(msg1));
		Assert.assertTrue(MessageFilter2.type(TestMessage.class).not.filter(msg1));
		Assert.assertTrue(MessageFilter2.type(TestMessage.class).and.filter(msg1));
	}

	@Test
	public void StartingWithMessageCombiner() {
		TestMessage msg1 = new TestMessage();

		Assert.assertTrue(MessageFilter2.not.type(TestMessage2.class).filter(msg1));
		Assert.assertFalse(MessageFilter2.not.type(TestMessage.class).filter(msg1));
	}

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
