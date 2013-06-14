package edu.asu.ying.mapreduce.rmi.activator.kad;

import edu.asu.ying.mapreduce.rmi.activator.Activator;

import java.io.Serializable;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;


/**
 * Controls creation and lifetime management for remotely activated objects.
 */
public class KadActivator
	implements Activator
{
	@Override
	public Remote getReference(final Class<?> type, final Map<String, String> properties) throws RemoteException {
		// TODO: use UnicastRemoteObject.exportObject
		System.out.println(String.format("GET INSTANCE: %s", type.toString()));
		return null;
	}

	@Override
	public URI getUri() {
		return null;
	}

	@Override
	public Serializable getResource() {
		return null;
	}
}
