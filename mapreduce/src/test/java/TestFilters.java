import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.filter2.FilterNode;
import edu.asu.ying.mapreduce.messaging.filter2.MessageFilter2;
import edu.asu.ying.mapreduce.rmi.resource.GetResourceMessage;
import edu.asu.ying.mapreduce.rmi.resource.GetResourceResponse;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import org.junit.Assert;
import org.junit.Test;

import edu.asu.ying.mapreduce.messaging.filter2.on;

/**
 *
 */
public class TestFilters
{
	@Test
	public void TheyCompose() throws Exception {
		final FilterNode filter =
				on.allOf(
					on.anyOf(
						on.type(GetResourceMessage.class),
						on.type(GetResourceResponse.class)
					),
					MessageFilter2.on.id("yes")
				);

		GetResourceMessage msg1 = new GetResourceMessage(new ResourceIdentifier("resource\\host\\path"));
		msg1.setId("yes");
		GetResourceMessage msg2 = new GetResourceMessage(new ResourceIdentifier("resource\\host\\path"));
		msg2.setId("no");
		GetResourceResponse rsp1 = new GetResourceResponse(msg1);
		rsp1.setId("yes");
		GetResourceResponse rsp2 = new GetResourceResponse(msg1);
		rsp2.setId("no");

		Assert.assertTrue(filter.match(msg1));
		Assert.assertFalse(filter.match(msg2));
		Assert.assertTrue(filter.match(rsp1));
		Assert.assertFalse(filter.match(rsp2));
	}
}
