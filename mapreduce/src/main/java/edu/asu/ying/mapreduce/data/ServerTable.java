package edu.asu.ying.mapreduce.data;

/**
 * {@link ServerTable} objects are the server-side implementation of {@link Table}. <p> Client nodes
 * can access {@link ServerTable} instances on obtaining a {@link java.rmi.Remote} reference from
 * the server node's {@link edu.asu.ying.mapreduce.rmi.Activator}. <p> Changes made to the
 * {@link ServerTable} via its {@link java.rmi.Remote} reference are persisted on the server node.
 */
public interface ServerTable {

}
