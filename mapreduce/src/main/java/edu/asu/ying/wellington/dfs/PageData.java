package edu.asu.ying.wellington.dfs;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.io.PageHeader;
import edu.asu.ying.wellington.io.Writable;

/**
 * {@code PageData} stores a page's metadata and binary data.
 */
public final class PageData implements Writable {

  /**
   * Constructs a {@link PageData} object from {@code page} and the entire contents of
   * {@code stream}.
   */
  public static PageData readDataFrom(Page page, InputStream stream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream(page.size());
    ByteStreams.copy(stream, buffer);
    return new PageData(page, buffer.toByteArray());
  }

  /**
   * Reads an entire page from serialized data, populating the metadata from the header.
   */
  public static PageData readFrom(DataInput in) throws IOException {
    PageData page = new PageData();
    page.readFields(in);
    return page;
  }

  // FIXME: hack to make distributionsink work with replicator
  private RemoteNode destination;
  private PageHeader header;
  private byte[] data;

  private PageData() {
  }

  public PageData(Page page, byte[] data) {
    this.data = data;
    // Verifies that data size is same as in metadata
    header = new PageHeader(page, data);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    // Header + data
    header = PageHeader.readFrom(in);
    data = new byte[header.getPage().size()];
    in.readFully(data);
    // Throw an exception if the read data is invalid
    header.validate(data);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    // Header + data
    header.write(out);
    out.write(data);
  }

  public PageHeader header() {
    return header;
  }

  public byte[] data() {
    return data;
  }

  public void setDestination(RemoteNode node) {
    destination = node;
  }

  public RemoteNode destination() {
    return destination;
  }
}
