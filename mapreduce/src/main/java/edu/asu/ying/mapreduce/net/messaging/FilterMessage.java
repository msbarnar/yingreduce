package edu.asu.ying.mapreduce.net.messaging;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterBase;
import edu.asu.ying.mapreduce.common.filter.FilterInteger;
import edu.asu.ying.mapreduce.common.filter.FilterString;


/**
 * {@code FilterMessage} provides common filters for {@link Message} types.
 */
public abstract class FilterMessage
    extends FilterBase {

  /**
   * Interface to message filters.
   */
  /**
   * Filters messages based on {@link edu.asu.ying.mapreduce.net.messaging.Message#getId()}.
   */
  public static Filter id(final FilterString filter) {
    return new FilterMessage.FilterOnId(filter);
  }

  /**
   * Filters messages based on the value of a specific property identified by {@code key}. </p>
   * Allows checking for null property values, though {@link edu.asu.ying.mapreduce.common.Properties}
   * does not allow them.
   */
  public static Filter property(final Serializable key, final @Nullable Serializable value) {
    Preconditions.checkNotNull(key);
    return new FilterMessage.FilterOnProperty(key, value);
  }

  /**
   * Filters messages based on {@link edu.asu.ying.mapreduce.net.messaging.Message#getDestinationUri()}.
   */
  public static final FilterOnUri destinationUri = FilterOnUri.onDestination;
  /**
   * Filters messages based on {@link edu.asu.ying.mapreduce.net.messaging.Message#getSourceUrl()}.
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

  private static final class FilterOnProperty extends FilterMessage {

    private final Serializable key, value;

    private FilterOnProperty(final Serializable key, final Serializable value) {
      this.key = key;
      this.value = value;
    }

    @Override
    protected boolean match(final Message message) {
      final Serializable messageValue = message.getProperties().get(this.key);
      if (this.value == null) {
        return messageValue == null;
      } else {
        return this.value.equals(messageValue);
      }
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
      Scheme,
      Address,
      Host,
      Port,
      Path,
      Name
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
    public final Filter scheme(final FilterString filter) {
      return new FilterOnUriPart(this.which, Part.Scheme, filter);
    }

    public final Filter address(final FilterString filter) {
      return new FilterOnUriPart(this.which, Part.Address, filter);
    }

    public final Filter host(final FilterString filter) {
      return new FilterOnUriPart(this.which, Part.Host, filter);
    }

    public final Filter port(final FilterInteger filter) {
      return new FilterOnUriPart(this.which, Part.Port, filter);
    }

    public final Filter path(final FilterString filter) {
      return new FilterOnUriPart(this.which, Part.Path, filter);
    }

    public final Filter name(final FilterString filter) {
      return new FilterOnUriPart(this.which, Part.Name, filter);
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
        final ResourceIdentifier uri;
        if (this.which == WhichUri.Destination) {
          uri = message.getDestinationUri();
        } else if (this.which == WhichUri.Source) {
          uri = message.getSourceUrl();
        } else {
          return false;
        }
        switch (this.part) {
          case Scheme:
            return this.filter.match(uri.getScheme());
          case Address:
            return this.filter.match(uri.getAddress());
          case Host:
            return this.filter.match(uri.getHost());
          case Port:
            return this.filter.match(uri.getPort());
          case Path:
            return this.filter.match(uri.getPath());
          case Name:
            return this.filter.match(uri.getName());
          default:
            return false;
        }
      }
    }
  }
  /*********************************************************/
}