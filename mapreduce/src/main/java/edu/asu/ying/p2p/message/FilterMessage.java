package edu.asu.ying.p2p.message;

import com.google.common.base.Preconditions;

import edu.asu.ying.common.filter.Filter;
import edu.asu.ying.common.filter.FilterBase;
import edu.asu.ying.common.filter.FilterString;
import edu.asu.ying.p2p.PeerIdentifier;


/**
 * {@code FilterMessage} provides common filters for {@link edu.asu.ying.p2p.message.Message}
 * types.
 */
public abstract class FilterMessage
    extends FilterBase {

  /**
   * Interface to message filters.
   */
  /**
   * Filters messages based on {@link edu.asu.ying.p2p.message.Message#getId()}.
   */
  public static Filter id(final FilterString filter) {
    return new FilterMessage.FilterOnId(filter);
  }

  /**
   * Filters messages based on {@link edu.asu.ying.p2p.message.Message#getDestination()}.
   */
  public static final FilterOnUri destinationUri = FilterOnUri.onDestination;
  /**
   * Filters messages based on {@link edu.asu.ying.p2p.message.Message#getSender()}.
   */
  public static final FilterOnUri sourceUri = FilterOnUri.onSource;

  /**
   * The filtering method for message filters.
   */
  protected abstract boolean match(final Message message);

  /**
   * Convenience function for message filters.
   */
  @Override
  public <V> boolean match(final V value) {
    if (!(value instanceof Message)) {
      return false;
    } else {
      return this.match((Message) value);
    }
  }

  /**
   * ****************************************************** Message Filters
   */
  private static final class FilterOnId extends FilterMessage {

    private final FilterString filter;

    private FilterOnId(final FilterString filter) {
      Preconditions.checkNotNull(filter);
      this.filter = filter;
    }

    @Override
    protected boolean match(final Message message) {
      return this.filter.match(message.getId());
    }
  }

  public static final class FilterOnUri {

    // Messages have multiple URIs
    private enum WhichUri {
      Source,
      Destination
    }

    // And URIs have multiple parts
    private enum Part {
      Key
    }

    /*
     * URI Selectors
     */
    public static final FilterOnUri onDestination = new FilterOnUri(WhichUri.Destination);
    public static final FilterOnUri onSource = new FilterOnUri(WhichUri.Source);

    /*
     * Constructor
     */
    private final WhichUri which;

    private FilterOnUri(final WhichUri which) {
      this.which = which;
    }

    /*
     * Part Selectors
     */
    public final Filter key(final FilterString filter) {
      return new FilterOnUriPart(this.which, Part.Key, filter);
    }

    /**
     * {@code FilterOnUriPart} does the actual URI filtering based on the composition of the URI and
     * Part selectors.
     */
    private final class FilterOnUriPart extends FilterMessage {

      private final WhichUri which;
      private final FilterOnUri.Part part;
      private final Filter filter;

      private FilterOnUriPart(final WhichUri which, final FilterOnUri.Part part,
                              final Filter filter) {
        this.which = which;
        this.part = part;
        this.filter = filter;
      }

      @Override
      protected boolean match(final Message message) {
        final PeerIdentifier uri;
        if (this.which == WhichUri.Destination) {
          uri = message.getDestination();
        } else if (this.which == WhichUri.Source) {
          uri = message.getSender();
        } else {
          return false;
        }
        switch (this.part) {
          case Key:
            return this.filter.match(uri.getKey());
          default:
            return false;
        }
      }
    }
  }
  /*********************************************************/
}