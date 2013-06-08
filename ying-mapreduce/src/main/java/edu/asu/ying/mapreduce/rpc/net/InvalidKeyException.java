package edu.asu.ying.mapreduce.rpc.net;


public final class InvalidKeyException
	extends IllegalArgumentException
{
	private static final long serialVersionUID = 6096714159265158893L;
	
	private final NetworkKey key;
	
	public InvalidKeyException() {
		super("Network key cannot be null or empty.");
		this.key = null;
	}
	public InvalidKeyException(final NetworkKey key) {
		this.key = key;
	}
	public InvalidKeyException(final String detail, final NetworkKey key) {
		super(detail);
		this.key = key;
	}
	public InvalidKeyException(final NetworkKey key, final Throwable cause) {
		super(cause);
		this.key = key;
	}
	public InvalidKeyException(final String detail, final NetworkKey key, final Throwable cause) {
		super(detail, cause);
		this.key = key;
	}
	
	public final NetworkKey getKey() { return this.key; }
}
