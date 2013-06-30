package edu.asu.ying.mapreduce.net.messaging.activator;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.messaging.MessageBase;
import edu.asu.ying.mapreduce.rmi.Activator;

/**
 *
 */
public final class ActivatorResponse extends MessageBase {

  private static final long SerialVersionUID = 1L;

  private static final class Property {
    private static final String ActivatorInstance = "activator.instance";
  }

  public static ActivatorResponse inResponseTo(final ActivatorRequest request) {
    return new ActivatorResponse(request.getSourceNode());
  }

  private ActivatorResponse(final NodeURI destinationNode) {
    super(destinationNode);
  }

  public final void setInstance(final Activator instance) {
    this.properties.put(Property.ActivatorInstance, Preconditions.checkNotNull(instance));
  }

  @Nullable
  public final Activator getInstance() {
    return this.properties.getDynamicCast(Property.ActivatorInstance, Activator.class);
  }
}
