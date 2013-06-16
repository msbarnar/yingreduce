import com.google.common.util.concurrent.ListenableFuture;
import edu.asu.ying.mapreduce.concurrency.FilteredFutures;
import edu.asu.ying.mapreduce.events.FilteredValueEvent;
import edu.asu.ying.mapreduce.messaging.filter.Filter;
import edu.asu.ying.mapreduce.messaging.filter.Filter.on;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


/**
 *
 */
public class TestFilteredFutures
{
	@Test
	public void ItReturnedTheRightOneOfThree() throws Exception {
		FilteredValueEvent<Integer> numberEvent = new FilteredValueEvent<>();

		Filter numberFilter = Filter.on.anyOf(on.doesEqual(1));

		final ListenableFuture<Integer> future = FilteredFutures.getFutureValuesFrom(numberEvent).filter(numberFilter).get(0);

		Assert.assertFalse(future.isDone());

		for (int i = 0; i < 3; i++) {
			numberEvent.fire(this, i);
		}

		Assert.assertTrue(future.isDone());
		Assert.assertEquals(1, (int)future.get());
	}

	@Test
	public void ItReturnedTheRightThreeOfNine() throws Exception {
		FilteredValueEvent<Integer> numberEvent = new FilteredValueEvent<>();

		Filter numberFilter = Filter.on.anyOf(on.doesEqual(1), on.doesEqual(3), on.doesEqual(6));

		List<ListenableFuture<Integer>> futureNumbers
				= FilteredFutures.getFutureValuesFrom(numberEvent).get(3).filter(numberFilter);

		for (final ListenableFuture<Integer> future : futureNumbers) {
			Assert.assertFalse(future.isDone());
		}

		for (int i = 0; i < 9; i++) {
			numberEvent.fire(this, i);
		}

		for (final ListenableFuture<Integer> future : futureNumbers) {
			Assert.assertTrue(future.isDone());
		}

		Assert.assertEquals(1, (int)futureNumbers.get(0).get());
		Assert.assertEquals(3, (int)futureNumbers.get(1).get());
		Assert.assertEquals(6, (int)futureNumbers.get(2).get());
	}
}
