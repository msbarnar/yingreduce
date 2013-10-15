package edu.asu.ying.common.remoting;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * {@code Activatable} classes can be used as {@link Remote} proxies or serialized across another
 * transport layer.
 */
public interface Activatable extends Remote, Serializable {

}
