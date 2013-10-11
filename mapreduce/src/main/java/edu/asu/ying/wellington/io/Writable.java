package edu.asu.ying.wellington.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 * {@code Writable} is an internal serialization protocol that enforces low per-item overhead. </p>
 * In the case of very numerous data, per-item overhead becomes the largest concern when serializing
 * to network or disk. Java serialization is unsuitable for this purpose, so the database is
 * comprised of few primitives which are efficiently serialized. Complex structures are created by
 * implementing {@code Writable} and composing primitives.
 */
public interface Writable extends Serializable {

  void readFields(DataInput in) throws IOException;

  void write(DataOutput out) throws IOException;
}
