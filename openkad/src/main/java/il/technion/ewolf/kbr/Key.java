package il.technion.ewolf.kbr;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

/**
 * Identifier for nodes. Use {@link KeyFactory} to generate instances of this
 * class.
 * 
 * @author eyal.kibbar@gmail.com
 */
public class Key implements Serializable, Comparable<Key> {

	private static final long serialVersionUID = 4137662182397711129L;
	private int color;
	private final byte[] bytes;
    private BigInteger bigInt = null;
    private String strRep = null;
    private String base64str = null;

	public Key(final byte[] bytes) {
		this.bytes = bytes;
	}
	public Key(final String base64) {
		this.bytes = Base64.decodeBase64(base64);
	}
	/**
	 * Check if a key is 0 key
	 * 
	 * @return true if bytes of this key are 0
	 */
	public boolean isZeroKey() {
		for (final byte x : getBytes())
			if (x != 0)
				return false;
		return true;
	}

	/**
	 * A key color is a number between 0 and nrColors that is calculated using
	 * its LSBs
	 * 
	 * @param nrColors
	 * @return the calculated color
	 */
	public int getColor(final int nrColors) {
		if (this.color < 0)
			this.color = Math.abs(getInt().intValue()) % nrColors;
		return this.color;
	}

	/**
	 * 
	 * @return all the key's bytes
	 */
	public byte[] getBytes() {
		return this.bytes;
	}

	/**
	 * 
	 * @return length of key in bytes
	 */
	public int getByteLength() {
		return this.bytes.length;
	}

	/**
	 * 
	 * @param k
	 *            another key
	 * @return a new Key which is the result of this key XOR the given key
	 */
	public Key xor(final Key k) {
		if (k.getByteLength() != this.bytes.length)
			throw new IllegalArgumentException("incompatable key for xor: keys are not the same length");
		final byte[] b = new byte[this.bytes.length];
		for (int i = 0; i < b.length; ++i)
			b[i] = (byte) (this.bytes[i] ^ k.getBytes()[i]);
		return new Key(b);
	}
	/**
	 * @return the index of the MSB turned on, or -1 if all bits are off
	 */
	public int getFirstSetBitIndex() {
		for (int i = 0; i < this.bytes.length; ++i) {
			if (this.bytes[i] == 0)
				continue;

			int j;
			for (j = 7; (this.bytes[i] & (1 << j)) == 0; --j);
			return (this.bytes.length - i - 1) * 8 + j;
		}
		return -1;
	}

	/**
	 * @return the key BigInteger representation
	 */
	public BigInteger getInt() {
      if (this.bigInt == null) {
        this.bigInt = new BigInteger(1, this.bytes);
      }
		return this.bigInt; // TODO: yoav is getBytes()
												// two-complement?
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !getClass().equals(o.getClass()))
			return false;
		return Arrays.equals(this.bytes, ((Key) o).getBytes());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.bytes);
	}

	/**
	 * 
	 * @return the key encode in Base64
	 */
	public String toBase64() {
      if (this.base64str == null) {
        this.base64str = Base64.encodeBase64String(this.bytes);
      }
		return this.base64str;
	}

	@Override
	public String toString() {
      if (this.strRep == null) {
        this.strRep = Base64.encodeBase64URLSafeString(this.bytes);
      }
		return this.strRep;
	}

	/**
	 * 
	 * @return the key encoded in binary string
	 */
	public String toBinaryString() {
		String $ = "";
		for (int i = 0; i < getByteLength(); ++i) {
			byte b = this.bytes[i];
			// fix negative numbers
			$ += b < 0 ? "1" : "0";
			b &= 0x7F;

			// fix insufficient leading 0s
			final String str = Integer.toBinaryString(b);
			switch (str.length()) {
				case 1 :
					$ += "000000";
					break;
				case 2 :
					$ += "00000";
					break;
				case 3 :
					$ += "0000";
					break;
				case 4 :
					$ += "000";
					break;
				case 5 :
					$ += "00";
					break;
				case 6 :
					$ += "0";
					break;
			}
			$ += str + " ";
		}
		return $;
	}

	@Override
	public int compareTo(final Key arg0) {
		return this.strRep.compareTo(arg0.toString());
	}
}
