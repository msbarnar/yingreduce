package edu.asu.ying.wellington;

/**
 *
 */
public class InvalidIdentifierException extends IllegalArgumentException {

  public InvalidIdentifierException(String message, String identifier) {
    super(String.format("%s: '%s'", message, identifier));
  }
}
