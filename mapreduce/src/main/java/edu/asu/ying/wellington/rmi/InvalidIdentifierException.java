package edu.asu.ying.wellington.rmi;

/**
 *
 */
public class InvalidIdentifierException extends IllegalArgumentException {

  public InvalidIdentifierException(String message, String identifier) {
    super(String.format("%s: '%s'", message, identifier));
  }
}
