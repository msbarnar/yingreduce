package edu.asu.barnard.samples.rmi;


public final class LetterCounter
	implements Mappable
{
	private static final long serialVersionUID = 1541891681372381719L;

	public LetterCounter() {
	}
	
	@Override
	public final Object apply(final Object value) {
		final String szValue = String.valueOf(value);
		if (szValue == null) {
			return -1;
		}
		return szValue.length();
	}
}
