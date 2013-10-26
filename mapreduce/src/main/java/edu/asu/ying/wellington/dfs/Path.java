package edu.asu.ying.wellington.dfs;

import com.google.common.base.Strings;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class Path implements WritableComparable<Path> {

  public static Path readFrom(DataInput in) throws IOException {
    Path path = new Path();
    path.readFields(in);
    return path;
  }

  private static final char DELIMITER_C = '/';
  private static final String DELIMITER = Character.toString(DELIMITER_C);
  private static final String ILLEGAL_CHARS = "";

  private String path;
  private String fileName;
  private List<String> directories;


  private Path() {
  }

  /**
   * Parses the path
   */
  public Path(String path) throws InvalidPathException {
    parse(path);
  }

  /**
   * Copy constructor
   */
  public Path(Path path) {
    this.path = path.path;
    this.fileName = path.fileName;
    this.directories = new ArrayList<>(path.directories);
  }

  /**
   * Returns a new path with {@code path} as a subpath of this one.
   */
  public Path resolve(String path) throws InvalidPathException {
    return new Path(concat(this.path, path));
  }

  private void parse(String path) throws InvalidPathException {
    // Strip extra whitespace, delimiters, fail on bad characters
    this.path = normalize(path);

    String[] components = this.path.split(DELIMITER);
    // All components but the last are directories
    this.directories = new ArrayList<>(components.length - 1);
    directories.addAll(Arrays.asList(components).subList(0, components.length - 1));

    // The last component is the filename
    this.fileName = components[components.length];
  }

  /**
   * Normalizes and concatenates the two path strings.
   * </p>
   * e.g. {@code "   /my///", "  //cool path//  "} becomes {@code "my/cool path"}
   */
  private String concat(String a, String b) throws InvalidPathException {
    return normalize(a).concat(DELIMITER).concat(normalize(b));
  }

  /**
   * <ul>
   * <li>Removes leading and trailing whitespace and trailing delimiters from a path.</li>
   * <li>Replaces multiple delimiters with a single delimiter.</li>
   * <li>Throws an exception on illegal characters.</li>
   * </ul>
   * </p>
   * e.g. {@code "  /my//cool path///   "} becomes {@code "my/cool path"}
   */
  private String normalize(String path) throws IllegalCharactersInPathException {
    if (Strings.nullToEmpty(path).isEmpty()) {
      throw new IllegalArgumentException("Path cannot be empty");
    }
    path = stripDelimiters(path.trim(), End.BOTH);
    List<Character> illegalCharacters = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    char lastChar = 0;
    for (char c : path.toCharArray()) {
      // Record all illegal characters in the string
      if (ILLEGAL_CHARS.indexOf(c) > -1) {
        illegalCharacters.add(c);
        continue;
      }
      // Fail fast for illegal characters
      if (!illegalCharacters.isEmpty()) {
        continue;
      }
      // Condense sequential delimiters to a single delimiter
      if (c == DELIMITER_C) {
        if (lastChar != DELIMITER_C) {
          sb.append(c);
        }
      }
      sb.append(c);
      lastChar = c;
    }

    if (!illegalCharacters.isEmpty()) {
      throw new IllegalCharactersInPathException(path, illegalCharacters);
    }

    // Stripping delimiters again makes "/" empty
    path = stripDelimiters(sb.toString(), End.BOTH);
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Path cannot be empty");
    }
    return path;
  }

  /**
   * Adds a single delimiter to {@code end}.
   */
  private String addDelimiter(String path, End end) {
    switch (end) {
      case LEADING:
        if (!path.startsWith(DELIMITER)) {
          return DELIMITER.concat(path);
        }
        return path;

      case TRAILING:
        if (!path.endsWith(DELIMITER)) {
          return path.concat(DELIMITER);
        }
        return path;

      case BOTH:
        path = addDelimiter(addDelimiter(path, End.TRAILING), End.LEADING);
        break;
    }
    return path;
  }

  /**
   * Removes all instances of the delimiter from {@code end}.
   */
  private String stripDelimiters(String path, End end) {
    switch (end) {
      case LEADING:
        while (path.startsWith(DELIMITER)) {
          path = path.substring(1);
        }
        break;

      case TRAILING:
        while (path.endsWith(DELIMITER)) {
          path = path.substring(0, path.length() - 1);
        }
        break;

      case BOTH:
        path = stripDelimiters(stripDelimiters(path, End.TRAILING), End.LEADING);
        break;
    }
    return path;
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || Path.class != o.getClass()) && path.equals(((Path) o).path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return path;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    parse(in.readUTF());
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(path);
  }

  @Override
  public int compareTo(Path o) {
    return path.compareTo(o.path);
  }

  private static enum End {
    LEADING,
    TRAILING,
    BOTH
  }
}
