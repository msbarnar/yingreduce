package edu.asu.ying.mapreduce.database.element;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;


public abstract class ElementException extends IOException {

  private final Collection<Element> elements;

  public ElementException() {
    this.elements = null;
  }

  public ElementException(final Element element) {
    this.elements = Arrays.asList(element);
  }

  public ElementException(final Collection<Element> elements) {
    this.elements = elements;
  }

  public final Collection<Element> getElements() {
    return this.elements;
  }
}
