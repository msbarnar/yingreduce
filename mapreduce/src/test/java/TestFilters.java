import edu.asu.ying.mapreduce.messaging.filter2.Filter;
import edu.asu.ying.mapreduce.messaging.filter2.Filter.on;
import edu.asu.ying.mapreduce.messaging.filter2.FilterMessage;
import edu.asu.ying.mapreduce.rmi.resource.GetResourceMessage;
import edu.asu.ying.mapreduce.rmi.resource.GetResourceResponse;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TestFilters
{
	@Test
	public void TheyCompose() throws Exception {
		final Filter filter =
				on.allOf(
						on.anyOf(
								on.classIs(GetResourceMessage.class),
								on.classIs(GetResourceResponse.class)
						),
						FilterMessage.on.id("yes"),
						FilterMessage.on.property("test", "hi")
				);

		GetResourceMessage msg1 = new GetResourceMessage(new ResourceIdentifier("resource\\host\\path"));
		msg1.setId("yes");
		msg1.getProperties().put("test", "hi");
		GetResourceMessage msg2 = new GetResourceMessage(new ResourceIdentifier("resource\\host\\path"));
		msg2.setId("no");
		GetResourceResponse rsp1 = new GetResourceResponse(msg1);
		rsp1.setId("yes");
		rsp1.getProperties().put("test", "hi");
		GetResourceResponse rsp2 = new GetResourceResponse(msg1);
		rsp2.setId("no");
		rsp2.getProperties().put("test", "bye");

		Assert.assertTrue(filter.match(msg1));
		Assert.assertFalse(filter.match(msg2));
		Assert.assertTrue(filter.match(rsp1));
		Assert.assertFalse(filter.match(rsp2));
	}

	@Test
	public void MatchAllOf() {
		final Filter filter =
				on.allOf(
						on.doesEqual(5),
						on.classIs(Integer.class)
				);
		Assert.assertTrue(filter.match(5));
		Assert.assertFalse(filter.match(50));
		Assert.assertFalse(filter.match(5.0));
	}

	@Test
	public void MatchAnyOf() {
		final Filter filter =
				on.anyOf(
						on.doesEqual(5),
						on.doesEqual(6),
						on.classIs(Double.class)
				);
		Assert.assertTrue(filter.match(5));
		Assert.assertTrue(filter.match(6));
		Assert.assertFalse(filter.match(7));
		Assert.assertTrue(filter.match(7.0));
	}

	@Test
	public void MatchNoneOf() {
		final Filter filter =
				on.noneOf(
						on.doesEqual(5),
						on.classIs(String.class)
				);
		Assert.assertTrue(filter.match(4));
		Assert.assertFalse(filter.match(5));
		Assert.assertTrue(filter.match(6));
		Assert.assertFalse(filter.match("6"));
	}
}
