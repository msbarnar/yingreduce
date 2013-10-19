package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Preconditions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.PageMetadata;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * Header format:
 * <pre>
 *   {@code
 *   Field           Length
 *   ----------------------
 *   Header length        2
 *   Magic value          4
 *   Version number       2
 *   Table name           n
 *   Page index           4
 *   Key class name       n
 *   Value class name     n
 *   Number of keys       4
 *   ----------------------
 *   page contents ...
 *   }
 * </pre>
 */
public final class PageHeader<K extends WritableComparable, V extends Writable> {

  public static PageHeader<?, ?> readFrom(InputStream stream) throws IOException {
    Preconditions.checkNotNull(stream);
    DataInputStream input;
    if (stream instanceof DataInputStream) {
      input = (DataInputStream) stream;
    } else {
      input = new DataInputStream(stream);
    }
    byte[] header = new byte[input.readShort()];
    int count = input.read(header, 0, header.length);
    if (count < header.length) {
      throw new EOFException("Incomplete page header");
    }

    return new PageHeader<>(header);
  }

  private static final int MAGIC = 0x4B494D53;
  private static final short VERSION = 1;

  private final byte[] header;

  private PageIdentifier pageID;
  private Class<K> keyClass;
  private Class<V> valueClass;
  private int numKeys;

  public PageHeader(PageMetadata<K, V> pageMetadata) throws IOException {
    this.pageID = pageMetadata.getID();
    this.keyClass = pageMetadata.getKeyClass();
    this.valueClass = pageMetadata.getValueClass();
    this.numKeys = pageMetadata.size();
    this.header = makeHeader();
  }

  private PageHeader(byte[] header) throws IOException {
    this.header = header;
    readFields();
  }

  public void writeTo(OutputStream stream) throws IOException {
    stream.write(header);
  }

  private byte[] makeHeader() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream output = new DataOutputStream(buffer);

    output.writeInt(MAGIC);
    output.writeShort(VERSION);
    output.writeUTF(pageID.getTableID().toString());
    output.writeInt(pageID.getIndex());
    output.writeUTF(keyClass.getCanonicalName());
    output.writeUTF(valueClass.getCanonicalName());
    output.writeInt(numKeys);

    byte[] header = buffer.toByteArray();
    buffer.close();

    ByteBuffer buf = ByteBuffer.allocate(2 + header.length);
    buf.putShort((short) header.length);
    buf.put(header);

    return buf.array();
  }

  @SuppressWarnings("unchecked")
  private void readFields() throws IOException {
    DataInputStream input = new DataInputStream(new ByteArrayInputStream(header));

    if (input.readInt() != MAGIC) {
      throw new NotPageDataException();
    }
    int version = input.readShort();
    if (version != VERSION) {
      throw new WrongPageVersionException(VERSION, version);
    }

    TableIdentifier tableID = TableIdentifier.forString(input.readUTF());
    int pageIndex = input.readInt();
    pageID = PageIdentifier.create(tableID, pageIndex);
    String keyClassName = input.readUTF();
    String valueClassName = input.readUTF();
    try {
      keyClass = (Class<K>) Class.forName(keyClassName);
    } catch (ClassNotFoundException | ClassCastException e) {
      throw new IOException("Key class not supported: ".concat(keyClassName), e);
    }
    try {
      valueClass = (Class<V>) Class.forName(valueClassName);
    } catch (ClassNotFoundException | ClassCastException e) {
      throw new IOException("Value class not supported: ".concat(valueClassName), e);
    }
    numKeys = input.readInt();
  }

  public PageIdentifier getPageID() {
    return pageID;
  }

  public Class<K> getKeyClass() {
    return keyClass;
  }

  public Class<V> getValueClass() {
    return valueClass;
  }

  public int getNumKeys() {
    return numKeys;
  }

  public static final class NotPageDataException extends IOException {

    public NotPageDataException() {
      super("The serialized data are not a page");
    }
  }

  public static final class WrongPageVersionException extends IOException {

    public WrongPageVersionException(int expected, int got) {
      super(
          String.format("PageMetadata data is wrong version: expected %d, got %d", expected, got));
    }
  }
}
