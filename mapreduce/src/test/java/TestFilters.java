import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterInteger;
import edu.asu.ying.mapreduce.common.filter.FilterString;
import edu.asu.ying.mapreduce.common.filter.Filter.on;
import edu.asu.ying.mapreduce.net.messaging.FilterMessage;
import edu.asu.ying.mapreduce.rmi.remote.NodeProxyRequest;

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
								FilterClass.is(NodeProxyRequest.class),
								FilterClass.is(ResourceResponse.class)
						),
						FilterMessage.id(FilterString.equalTo("yes")),
						FilterMessage.property("test", "hi")
				);

		NodeProxyRequest
            msg1 = NodeProxyRequest.locatedBy(new ResourceIdentifier("resource\\host\\path"));
		msg1.setSourceUri("node\\localhost");
		msg1.setId("yes");
		msg1.getProperties().put("test", "hi");
		NodeProxyRequest
            msg2 = NodeProxyRequest.locatedBy(new ResourceIdentifier("resource\\host\\path"));
		msg2.setSourceUri("node\\localhost");
		msg2.setId("no");
		ResourceResponse rsp1 = ResourceResponse.inResponseTo(msg1);
		rsp1.setId("yes");
		rsp1.getProperties().put("test", "hi");
		ResourceResponse rsp2 = ResourceResponse.inResponseTo(msg2);
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
						FilterInteger.equalTo(5),
						FilterClass.is(Integer.class)
				);
		Assert.assertTrue(filter.match(5));
		Assert.assertFalse(filter.match(50));
		Assert.assertFalse(filter.match(5.0));
	}

	@Test
	public void MatchAnyOf() {
		final Filter filter =
				on.anyOf(
                    FilterInteger.equalTo(5),
                    FilterInteger.equalTo(6),
                    FilterClass.is(Double.class)
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
                    FilterInteger.equalTo(5),
                    FilterClass.is(String.class)
				);
		Assert.assertTrue(filter.match(4));
		Assert.assertFalse(filter.match(5));
		Assert.assertTrue(filter.match(6));
		Assert.assertFalse(filter.match("6"));
	}
}
