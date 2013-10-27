package edu.asu.ying.wellington.dfs.io;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.wellington.dfs.ChecksumMismatchException;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.rmi.VersionMismatchException;

/**
 * Header format:
 * <pre>
 *   {@code
 *   Field           Length
 *   ----------------------
 *   Magic            24
 *   Version          8
 *   Checksum of {    32
 *   Page
 *   Data
 *   }
 *   Page
 *   ----------------------
 *   - binary data -
 *   }
 * </pre>
 */
public final class PageHeader implements Writable {

  public static PageHeader readFrom(DataInput in) throws IOException {
    PageHeader pageHeader = new PageHeader();
    pageHeader.readFields(in);
    return pageHeader;
  }

  private static final Logger log = Logger.getLogger(PageHeader.class.getName());

  private static final int MAGIC = 0x4B494D;
  private static final byte VERSION = 1;

  private Page page;

  private final HashFunction checksumFunc = Hashing.adler32();
  private int checksum;

  private PageHeader() {
  }

  public PageHeader(Page page, byte[] data) {
    if (page.size() != data.length) {
      throw new IllegalArgumentException("Page size does not match data size: " + page.toString());
    }
    this.page = page;
    // Compute checksum
    this.checksum = computeChecksum(page, data);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    // Magic
    out.writeInt((MAGIC << 8) | VERSION);
    // Checksum
    out.writeInt(checksum);
    // Page (includes file)
    page.write(out);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    // Magic
    int magicVersion = in.readInt();
    if ((magicVersion >> 8) != MAGIC) {
      throw new NotPageDataException();
    }
    int version = magicVersion & 0xFF;
    if (version != VERSION) {
      throw new VersionMismatchException(VERSION, version);
    }
    // Checksum
    checksum = in.readInt();
    // Page
    page = Page.readFrom(in);
  }

  public Page getPage() {
    return page;
  }

  /**
   * Validates the checksum of the current page metadata + {@code data}.
   */
  public void validate(byte[] data) throws IOException {
    int actual = computeChecksum(page, data);
    if (checksum != actual) {
      throw new ChecksumMismatchException(checksum, actual);
    }
  }

  /**
   * Serializes the page metadata computes the checksum of the {@code page} metadata + {@code
   * data}.
   */
  private int computeChecksum(Page page, byte[] data) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (DataOutputStream out = new DataOutputStream(buffer)) {
      page.write(out);
    } catch (IOException e) {
      // Should never happen
      log.log(Level.WARNING, "Exception serializing page metadata for checksum", e);
      return -1;
    }
    Hasher checksummer = checksumFunc.newHasher();
    checksummer.putBytes(buffer.toByteArray());
    checksummer.putBytes(data);

    return checksummer.hash().asInt();
  }

  public static final class NotPageDataException extends IOException {

    public NotPageDataException() {
      super("The serialized data are not a page");
    }
  }
}
