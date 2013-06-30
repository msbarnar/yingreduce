package edu.asu.ying.mapreduce.net.resources;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * A {@link RemoteResource} is a reference to a resources that exists outside the current context
 * and can be discovered, accessed, or referenced using a {@link ResourceIdentifier}. <p> Required
 * parts of a resources URI are: <ul> <li>{@code scheme} - is always {@code resources}.</li>
 * <li>{@code host} - describes the location information for the resources; its meaning is
 * implementation specific. </li> <li>{@code path} - is the resources type.</li> <li>{@code query} -
 * the specific resources name, if necessary.</li> </ul> <p> Optional parts: <ul> <li>{@code
 * authority} - specifies authorization parameters for resources that are access-controlled.</li>
 * </ul> <p> E.g. the resources {@code MyImage} of type {@code image} on host {@code localhost} and
 * port {@code 9000} would appear as such: <p> {@code resources://localhost:9000/image?MyImage}
 */
public interface RemoteResource
    extends Remote, Serializable {

  ResourceIdentifier getResourceUri() throws RemoteException;
}
