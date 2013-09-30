package edu.asu.ying.mapreduce.database.page;

import edu.asu.ying.mapreduce.database.element.Element;
import edu.asu.ying.mapreduce.database.element.ElementException;

/**
 *
 */
public class PageCapacityExceededException extends ElementException {

  public PageCapacityExceededException(final Element element) {
    super(element);
  }
}
