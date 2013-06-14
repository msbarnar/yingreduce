package edu.asu.ying.mapreduce.data;

/**
 * {@link RemoteTable} objects are the server-side implementation of {@link DistributedTable}.
 * <p>
 * Client nodes can access {@link RemoteTable} instances by obtaining a {@link java.rmi.Remote} reference from the
 * server node's {@link edu.asu.ying.mapreduce.rmi.activator.Activator}.
 * <p>
 * Changes made to the {@link RemoteTable} via its {@link java.rmi.Remote} reference are persisted on the server node.
 */
public interface RemoteTable
{
}
