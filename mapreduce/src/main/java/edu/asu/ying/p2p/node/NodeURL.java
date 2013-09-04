package edu.asu.ying.p2p.node;

import java.net.URI;

import edu.asu.ying.p2p.NodeIdentifier;

/**
 * A {@code NodeURL} identifies the exact location of and means of reaching a specific node, in
 * addition to uniquely identifying the node per its {@link edu.asu.ying.p2p.NodeIdentifier}.
 */
public interface NodeURL extends NodeIdentifier {

  URI toURI();
}
