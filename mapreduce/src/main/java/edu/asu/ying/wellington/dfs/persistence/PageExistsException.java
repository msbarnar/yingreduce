package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public class PageExistsException extends IOException {

  public PageExistsException(PageIdentifier id) {
    super(id.toString());
  }
}
