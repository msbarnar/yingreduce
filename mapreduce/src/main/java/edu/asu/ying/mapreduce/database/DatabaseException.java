package edu.asu.ying.mapreduce.database;

import java.io.IOException;

/**
 *
 */
public class DatabaseException extends IOException {

  public DatabaseException() {
  }

  public DatabaseException(final Throwable cause) {
    super(cause);
  }

  public DatabaseException(final String message) {
    super(message);
  }

  public DatabaseException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
