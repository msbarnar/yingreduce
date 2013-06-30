package edu.asu.ying.mapreduce.net.resource.server;

/**
 * {@code ResourceRequestHandler} is the scheduling-side {@link edu.asu.ying.mapreduce.net.resource.RemoteResource}
 * factory. </p> {@code ResourceRequestHandler} objects listen to {@link
 * edu.asu.ying.mapreduce.net.messaging.IncomingMessageEvent} and respond to {@link
 * edu.asu.ying.mapreduce.net.resource.ResourceRequest} messages by provisioning instances of
 * resource. </p> The primary role of the {@code ResourceRequestHandler} is to provide {@link
 * java.rmi.Remote} references to the scheduling node's {@link edu.asu.ying.mapreduce.rmi.Activator},
 * but the resource framework is meant to be able to provide anything to a client node.
 */
public interface ResourceRequestHandler {

}
