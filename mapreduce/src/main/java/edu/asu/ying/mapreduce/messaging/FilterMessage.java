package edu.asu.ying.mapreduce.messaging;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterBase;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;

import javax.annotation.Nullable;
import java.io.Serializable;


/**
 * {@code FilterMessage} provides common filters for {@link Message} types.
 */
public abstract class FilterMessage
	extends FilterBase
{
	/**
	 * Interface to message filters.
	 */
	public static class on
	{
		/**
		 * Filters messages based on {@link edu.asu.ying.mapreduce.messaging.Message#getId()}.
		 */
		public static Filter id(final String id) {
			Preconditions.checkNotNull(id);
			return new FilterMessage.FilterOnId(id);
		}
		/**
		 * Filters messages based on the value of a specific property identified by {@code key}.
		 * </p>
		 * Allows checking for null property values, though {@link edu.asu.ying.mapreduce.common.Properties}
		 * does not allow them.
		 */
		public static Filter property(final Serializable key, final @Nullable Serializable value) {
			Preconditions.checkNotNull(key);
			return new FilterMessage.FilterOnProperty(key, value);
		}

		/**
		 * Filters messages based on {@link edu.asu.ying.mapreduce.messaging.Message#getDestinationUri()}.
		 */
		public static final FilterOnUri destinationUri = FilterOnUri.onDestination;
		/**
		 * Filters messages based on {@link edu.asu.ying.mapreduce.messaging.Message#getSourceUri()}.
		 */
		public static final FilterOnUri sourceUri = FilterOnUri.onSource;
	}

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

	/*********************************************************
	 * Message Filters
	 */
	private static final class FilterOnId extends FilterMessage
	{
		private final String id;
		private FilterOnId(final String id) {
			Preconditions.checkNotNull(Strings.emptyToNull(id));
			this.id = id;
		}

		@Override protected boolean match(final Message message) {
			return this.id.equals(message.getId());
		}
	}

	private static final class FilterOnProperty extends FilterMessage
	{
		private final Serializable key, value;

		private FilterOnProperty(final Serializable key, final Serializable value) {
			this.key = key;
			this.value = value;
		}

		@Override protected boolean match(final Message message) {
			final Serializable messageValue = message.getProperties().get(this.key);
			if (this.value == null) {
				return messageValue == null;
			} else {
				return this.value.equals(messageValue);
			}
		}
	}

	private static final class FilterOnUri
	{
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
		public final Filter scheme(final String scheme) {
			Preconditions.checkNotNull(scheme);
			return new FilterOnUriPart(this.which, Part.Scheme, scheme);
		}
		public final Filter address(final String address) {
			Preconditions.checkNotNull(address);
			return new FilterOnUriPart(this.which, Part.Address, address);
		}
		public final Filter host(final String host) {
			Preconditions.checkNotNull(host);
			return new FilterOnUriPart(this.which, Part.Host, host);
		}
		public final Filter port(final int port) {
			return new FilterOnUriPart(this.which, Part.Port, port);
		}
		public final Filter path(final String path) {
			Preconditions.checkNotNull(path);
			return new FilterOnUriPart(this.which, Part.Path, path);
		}
		public final Filter name(final String name) {
			Preconditions.checkNotNull(name);
			return new FilterOnUriPart(this.which, Part.Name, name);
		}

		/**
		 * {@code FilterOnUriPart} does the actual URI filtering based on the composition of the URI and Part selectors.
		 */
		private final class FilterOnUriPart extends FilterMessage {
			private final WhichUri which;
			private final FilterOnUri.Part part;
			private final Object value;

			private FilterOnUriPart(final WhichUri which, final FilterOnUri.Part part, final Object value) {
				this.which = which;
				this.part = part;
				this.value = value;
			}

			@Override
			protected boolean match(final Message message) {
				final ResourceIdentifier uri;
				if (this.which == WhichUri.Destination) {
					uri = message.getDestinationUri();
				} else if (this.which == WhichUri.Source) {
					uri = message.getSourceUri();
				} else {
					return false;
				}
				switch (this.part) {
					case Scheme: return this.value.equals(uri.getScheme());
					case Address: return this.value.equals(uri.getAddress());
					case Host: return this.value.equals(uri.getHost());
					case Port: return this.value.equals(uri.getPort());
					case Path: return this.value.equals(uri.getPath());
					case Name: return this.value.equals(uri.getName());
					default: return false;
				}
			}
		}
	}
	/*********************************************************/
}