package edu.asu.ying.wellington.dfs.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.TableIdentifier;

/**
 *
 */
public final class PageHeader {

  public static PageHeader readFrom(InputStream stream) throws IOException {
    DataInputStream input = new DataInputStream(stream);
    byte[] header = new byte[input.readInt()];
    input.readFully(header);

    return new PageHeader(header);
  }

  private static final int MAGIC = 0x4B494D53;
  private static final int VERSION = 1;

  private final byte[] header;

  private PageIdentifier pageID;
  private int numKeys;

  public PageHeader(Page page) throws IOException {
    this.pageID = page.getID();
    this.numKeys = page.getNumKeys();
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
    output.writeInt(VERSION);
    output.writeUTF(pageID.getTableID().toString());
    output.writeInt(pageID.getIndex());
    output.writeInt(numKeys);

    byte[] header = buffer.toByteArray();
    buffer.close();

    ByteBuffer buf = ByteBuffer.allocate(4 + header.length);
    buf.putInt(4 + header.length);
    buf.put(header);

    return buf.array();
  }

  private void readFields() throws IOException {
    DataInputStream input = new DataInputStream(new ByteArrayInputStream(header));

    if (input.readInt() != MAGIC) {
      throw new NotPageDataException();
    }
    int version = input.readInt();
    if (version != VERSION) {
      throw new WrongPageVersionException(VERSION, version);
    }

    TableIdentifier tableID = TableIdentifier.forString(input.readUTF());
    int pageIndex = input.readInt();
    pageID = PageIdentifier.create(tableID, pageIndex);
    numKeys = input.readInt();
  }

  public PageIdentifier getPageID() {
    return pageID;
  }

  public int getNumKeys() {
    return numKeys;
  }

  public static final class NotPageDataException extends IOException {

  }

  public static final class WrongPageVersionException extends IOException {

    public WrongPageVersionException(int expected, int got) {
      super(String.format("Page data is wrong version: expected %d, got %d", expected, got));
    }
  }
}
