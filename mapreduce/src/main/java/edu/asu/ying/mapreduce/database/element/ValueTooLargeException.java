package edu.asu.ying.mapreduce.database.element;

import java.util.Collection;

import edu.asu.ying.mapreduce.database.element.Element;
import edu.asu.ying.mapreduce.database.element.ElementException;

/**
 *
 */
public class ValueTooLargeException extends ElementException {

  public ValueTooLargeException(final Element element) {
    super(element);
  }

  public ValueTooLargeException(final Collection<Element> elements) {
    super(elements);
  }
}
