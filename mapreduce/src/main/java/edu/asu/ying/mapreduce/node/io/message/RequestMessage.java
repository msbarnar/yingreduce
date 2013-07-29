package edu.asu.ying.mapreduce.node.io.message;

import edu.asu.ying.mapreduce.node.NodeURI;

/**
 *
 */
public class RequestMessage extends MessageBase {

  public RequestMessage(String tag) {
    super(tag);
  }

  public RequestMessage(String id, String tag) {
    super(id, tag);
  }

  public RequestMessage(String tag, NodeURI destinationNode) {
    super(tag, destinationNode);
  }

  public RequestMessage(String id, String tag,
                        NodeURI destinationNode) {
    
    super(id, tag, destinationNode);
  }
}
