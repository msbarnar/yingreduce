package edu.asu.ying.test;

import com.google.common.util.concurrent.ListenableFuture;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import edu.asu.ying.common.concurrency.FilteredFutures;
import edu.asu.ying.common.event.FilteredValueEvent;
import edu.asu.ying.common.event.FilteredValueEventBase;
import edu.asu.ying.common.filter.Filter;
import edu.asu.ying.common.filter.Filter.on;


/**
 *
 */
public class TestFilteredFutures {

  @Test
  public void ItReturnedTheRightOneOfThree() throws Exception {
    FilteredValueEvent<Integer> numberEvent = new FilteredValueEventBase<>();

    Filter numberFilter = Filter.on.anyOf(on.equalTo(1));

    final
    ListenableFuture<Integer>
        future =
        FilteredFutures.getFrom(numberEvent).filter(numberFilter).get(0);

    Assert.assertFalse(future.isDone());

    for (int i = 0; i < 3; i++) {
      numberEvent.fire(this, i);
    }

    Assert.assertTrue(future.isDone());
    Assert.assertEquals(1, (int) future.get());
  }

  @Test
  public void ItReturnedTheRightThreeOfNine() throws Exception {
    FilteredValueEvent<Integer> numberEvent = new FilteredValueEventBase<>();

    Filter numberFilter = Filter.on.anyOf(on.equalTo(1), on.equalTo(3), on.equalTo(6));

    List<ListenableFuture<Integer>> futureNumbers
        = FilteredFutures.getFrom(numberEvent).get(3).filter(numberFilter);

    for (final ListenableFuture<Integer> future : futureNumbers) {
      Assert.assertFalse(future.isDone());
    }

    for (int i = 0; i < 9; i++) {
      numberEvent.fire(this, i);
    }

    for (final ListenableFuture<Integer> future : futureNumbers) {
      Assert.assertTrue(future.isDone());
    }

    Assert.assertEquals(1, (int) futureNumbers.get(0).get());
    Assert.assertEquals(3, (int) futureNumbers.get(1).get());
    Assert.assertEquals(6, (int) futureNumbers.get(2).get());
  }
}
