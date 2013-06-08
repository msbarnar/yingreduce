package edu.asu.ying.mapreduce.rpc.net;

public class NetworkException
	extends RuntimeException
{
	private static final long serialVersionUID = 2420540131218119570L;

	public NetworkException() {
	}
	public NetworkException(final String detail) {
		super(detail);
	}
	public NetworkException(final String detail, final Throwable cause) {
		super(detail, cause);
	}
	public NetworkException(final Throwable cause) {
		super(cause);
	}
}
