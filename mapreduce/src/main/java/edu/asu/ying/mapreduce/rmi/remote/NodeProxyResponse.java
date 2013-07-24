package edu.asu.ying.mapreduce.rmi.remote;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.messaging.MessageBase;
import edu.asu.ying.mapreduce.rmi.Activator;

/**
 *
 */
public final class NodeProxyResponse extends MessageBase {

  private static final long SerialVersionUID = 1L;

  private static final class Property {
    private static final String Instance = "remote.instance";
  }

  public static NodeProxyResponse inResponseTo(final NodeProxyRequest request) {
    return new NodeProxyResponse(request.getSourceNode());
  }

  private NodeProxyResponse(final NodeURI destinationNode) {
    super(destinationNode);
  }

  public final void setInstance(final NodeProxy instance) {
    this.properties.put(Property.Instance, Preconditions.checkNotNull(instance));
  }

  @Nullable
  public final NodeProxy getInstance() {
    return this.properties.getDynamicCast(Property.Instance, Activator.class);
  }
}
