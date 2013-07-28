package edu.asu.ying.mapreduce.yingtable;

/**
 * Thrown when attempting to add an element to a page which would cause the page to exceed its
 * maximum capacity.
 * </p>
 * The appropriate course of action in this case would be to commit the page and begin a new page,
 * starting with the element that caused this exception.
 */
public abstract class ElementException extends Exception {

  private final Element element;

  public ElementException() {
    this.element = null;
  }

  public ElementException(final Element element) {
    this.element = element;
  }

  public final Element getElement() {
    return this.element;
  }
}
