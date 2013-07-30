package edu.asu.ying.mapreduce.node.rmi;

import java.io.Serializable;
import java.rmi.Remote;

import edu.asu.ying.mapreduce.node.Node;

/**
 *
 */
public interface NodeProxy extends Node, Remote, Serializable {

}
