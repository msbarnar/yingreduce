package edu.asu.ying.wellington.service;

/**
 * A {@code ServiceConnector} is the {@link ServiceLocator}'s point of access for service retrieval
 * from an underlying model. The model exposes connectors for each registered service, and the
 * connector abstracts the instance access from the actual instance retrieval.
 * </p>
 * e.g. a service connector from the RMI model retrieves a remote instance from RMI and provides
 * it to the service locator, allowing the service locator to be abstracted from RMI.
 * </p>
 * Each service connector is connected to one service instance.
 */
public interface ServiceConnector<T extends Service> {

  T getInstance() throws ServiceException;
}
