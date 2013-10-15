package edu.asu.ying.wellington.dfs.io;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.Element;

/**
 *
 */
public interface ElementOutputStream {

  void write(Element element) throws IOException;

  int write(Iterable<Element> elements) throws IOException;

  void flush() throws IOException;
}
