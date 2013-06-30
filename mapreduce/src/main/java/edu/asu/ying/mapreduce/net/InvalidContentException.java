package edu.asu.ying.mapreduce.net;

import java.io.IOException;


/**
 * Signals to a message source that its message was malformed. <p> In practice, this generally means
 * the content did not derive from {@link edu.asu.ying.mapreduce.net.messaging.Message}.
 */
public class InvalidContentException
    extends IOException {

}
