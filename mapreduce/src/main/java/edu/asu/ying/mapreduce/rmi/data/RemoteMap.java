package edu.asu.ying.mapreduce.rmi.data;

/**
 * {@link RemoteMap} objects are the server-side implementation of {@link edu.asu.ying.mapreduce.data.DistributedMap}.
 * <p>
 * Client nodes can access {@link RemoteMap} instances by obtaining a {@link java.rmi.Remote} reference from the
 * server node's {@link edu.asu.ying.mapreduce.rmi.activator.Activator}.
 * <p>
 * Changes made to the {@link RemoteMap} via its {@link java.rmi.Remote} reference are persisted on the server node.
 */
public interface RemoteMap
{
}
