package edu.asu.ying.mapreduce.net.resources.server;

/**
 * {@code ResourceRequestHandler} is the server-side {@link edu.asu.ying.mapreduce.net.resources.RemoteResource}
 * factory. </p> {@code ResourceRequestHandler} objects listen to {@link
 * edu.asu.ying.mapreduce.net.messaging.IncomingMessageEvent} and respond to {@link
 * edu.asu.ying.mapreduce.net.resources.ResourceRequest} messages by provisioning instances of
 * resources. </p> The primary role of the {@code ResourceRequestHandler} is to provide {@link
 * java.rmi.Remote} references to the server node's {@link edu.asu.ying.mapreduce.rmi.activator.Activator},
 * but the resource framework is meant to be able to provide anything to a client node.
 */
public interface ResourceRequestHandler {

}
