package edu.asu.ying.mapreduce.rmi.activator;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import edu.asu.ying.mapreduce.messaging.*;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.rmi.resource.ResourceResponse;
import edu.asu.ying.mapreduce.rmi.resource.ResourceProvider;
import edu.asu.ying.mapreduce.rmi.resource.ResourceRequest;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * The {@link ActivatorProvider} intercepts {@link edu.asu.ying.mapreduce.rmi.resource.ResourceRequest} and
 * interprets requests for {@link Activator} references.
 */
public final class ActivatorProvider
		//implements ResourceProvider, FutureCallback<Message>
{
}
