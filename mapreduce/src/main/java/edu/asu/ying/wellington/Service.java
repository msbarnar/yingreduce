package edu.asu.ying.wellington;

/**
 * Specifies a service which requires starting.
 */
public interface Service {

  /**
   * Starts the service with a reference to the node on which the service is running.
   */
  void start();
}
