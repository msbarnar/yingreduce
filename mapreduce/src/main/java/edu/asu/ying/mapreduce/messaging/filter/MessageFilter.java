package edu.asu.ying.mapreduce.messaging.filter;


import edu.asu.ying.mapreduce.messaging.Message;


/**
 * The root message filter; provides Any or All filters.
 */
public class MessageFilter
{
	public final AbstractMessageFilter allOf = new MessageFilterAllOf();
	public final AbstractMessageFilter anyOf = new MessageFilterAnyOf();
	public final AbstractMessageFilter noneOf = new MessageFilterNoneOf();

	private AbstractMessageFilter setFilter;

	private boolean matchAny = false;

	public MessageFilter() {
		this.allOf.bind(this);
		this.anyOf.bind(this);
		this.noneOf.bind(this);
	}

	public final void clear() {
		this.setFilter = null;
		this.allOf.clear();
		this.anyOf.clear();
		this.noneOf.clear();
		this.matchAny = false;
	}

	public final void any() {
		this.clear();
		this.matchAny = true;
	}

	public final void set(final AbstractMessageFilter filter) {
		this.setFilter = filter;
	}

	public boolean match(final Message message) {
		if (this.setFilter != null) {
			return this.setFilter.match(message);
		}

		if (allOf.isActive() || anyOf.isActive() || noneOf.isActive()) {
			return this.anyOf.match(message) && this.noneOf.match(message) && this.allOf.match(message);
		} else {
			if (this.matchAny) {
				return true;
			} else {
				// Match nothing by default
				return false;
			}
		}
	}
}
