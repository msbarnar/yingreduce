package edu.asu.ying.mapreduce.ui.http;

public class HttpServerException
	extends RuntimeException
{
	private static final long serialVersionUID = 4172613285134635237L;
	
	public HttpServerException() {
	}
	public HttpServerException(final String detail) {
		super(detail);
	}
	public HttpServerException(final String detail, final Throwable cause) {
		super(detail, cause);
	}
	public HttpServerException(final Throwable cause) {
		super(cause);
	}
}
