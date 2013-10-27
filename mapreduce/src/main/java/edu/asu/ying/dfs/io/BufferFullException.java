package edu.asu.ying.dfs.io;

import java.io.IOException;

/**
 * Signals that a {@link PageDistributionStream}'s capacity would be exceeded by the attempted
 * write.
 */
public class BufferFullException extends IOException {

}
